package com.example.gympt.domain.member.service;

import com.example.gympt.domain.category.entity.Local;
import com.example.gympt.domain.category.repository.LocalRepository;
import com.example.gympt.domain.gym.entity.Gym;
import com.example.gympt.domain.gym.entity.GymImage;
import com.example.gympt.domain.gym.repository.GymRepository;
import com.example.gympt.domain.member.dto.CreateGymDTO;
import com.example.gympt.domain.member.entity.Member;
import com.example.gympt.domain.member.enums.MemberRole;
import com.example.gympt.domain.member.repository.MemberRepository;
import com.example.gympt.domain.trainer.dto.TrainerSaveFormDTO;
import com.example.gympt.domain.trainer.entity.TrainerSaveForm;
import com.example.gympt.domain.trainer.entity.TrainerSaveImage;
import com.example.gympt.domain.trainer.entity.Trainers;
import com.example.gympt.domain.trainer.enums.Gender;
import com.example.gympt.domain.trainer.repository.TrainerRepository;
import com.example.gympt.domain.trainer.repository.TrainerSaveFormRepository;
import com.example.gympt.util.s3.CustomFileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final MemberRepository memberRepository;
    private final GymRepository gymRepository;
    private final TrainerRepository trainerRepository;
    private final TrainerSaveFormRepository trainerSaveFormRepository;
    private final LocalRepository localRepository;
    private final CustomFileUtil customFileUtil;

    @Transactional
    @Override
    //트레이너 권한 바꿔주기 + 트레이너 테이블에 새로운 트레이너 추가
    public void approveTrainer(String trainerEmail, String adminUsername) {
        // 관리자 확인
        Member admin = memberRepository.getWithRoles(adminUsername)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다"));

        if (!admin.getMemberRoleList().contains(MemberRole.ADMIN)) {
            throw new RuntimeException("관리자 권한이 없습니다");
        }

        // 트레이너 승인 대상 확인
        Member trainer = memberRepository.findByEmail(trainerEmail)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다"));

        if (!trainer.getMemberRoleList().contains(MemberRole.PREPARATION_TRAINER)) {
            throw new RuntimeException("승인 대상이 아닙니다");
        }

        // PREPARATION_TRAINER -> TRAINER 권한 변경
        trainer.getMemberRoleList().remove(MemberRole.PREPARATION_TRAINER);
        trainer.getMemberRoleList().add(MemberRole.TRAINER);
        memberRepository.save(trainer); // 권한 바꿔주기
        saveTrainer(trainerEmail);
    }


    public void saveTrainer(String trainerEmail) {
        Member member = memberRepository.getWithRoles(trainerEmail)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다"));
        TrainerSaveForm trainerSaveForm = trainerSaveFormRepository.findByEmail(trainerEmail)
                .orElseThrow(() -> new RuntimeException("신청 내역이 없습니다"));

        Gym gym = gymRepository.findByGymId(trainerSaveForm.getGym().getId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 헬스장입니다"));

        List<TrainerSaveImage> images = trainerSaveForm.getImageList();

        Trainers trainer = new Trainers();
        trainer.setTrainerName(trainerSaveForm.getName());
        trainer.setAge(trainerSaveForm.getAge());
        trainer.setIntroduction(trainerSaveForm.getIntroduction());
        trainer.setGender(Gender.valueOf(trainerSaveForm.getGender()));
        trainer.setMember(member);
        trainer.setGym(gym);

        if (images != null && !images.isEmpty()) {
//TrainerSaveForm 이미지를 trainer 엔티티에도 저장
            List<String> imageNames = images.stream().map(TrainerSaveImage::getTrainerSaveImageName).toList();
            //트레이너 신청 이미지 리스트에서 이미지 이름만 가져와서 문자열 리스트로 만듦
            List<String> savedImageList = customFileUtil.uploadImagePathS3Files(imageNames);
            //이미지 이름 문자열 리스트를 s3 에 업로드 함

            for (String saveImageName : savedImageList) {
                //savedImageList 에 있는 문자열들을 하나씩 꺼내서
                trainer.addImageString(saveImageName);
                //트레이너 테이블에 저장
            }
        }
        trainerRepository.save(trainer);
    }


//트레이너 회원가입을 따로 안만든 이유 :
//카카오 소셜 로그인도 있어서 같이 연동하려면 너무 복잡해지기 때문에
//(kakao) member -> trainer 형식으로 구현
//트레이너 신청시 TrainerSaveForm 에 insert -> admin 의 허용시 Trainers 에 트레이너 정보가 그대로 insert 된다


    //트레이너 신청 목록 리스트로 보기 !!!!
    @Override
    public List<TrainerSaveFormDTO> getPreparationTrainers(String adminUsername) {
        Member admin = memberRepository.getWithRoles(adminUsername)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다"));

        if (!admin.getMemberRoleList().contains(MemberRole.ADMIN)) {
            throw new RuntimeException("관리자 권한이 없습니다");
        }

        List<TrainerSaveFormDTO> trainerSaveFormDTOList = trainerSaveFormRepository.findAll()
                .stream().map(this::converTodto).collect(Collectors.toList());
        return trainerSaveFormDTOList;

    }

    //새 헬스장 등록
    @Override
    public void createGym(CreateGymDTO createGymDTO, String adminUsername) {
        Member admin = memberRepository.getWithRoles(adminUsername)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다"));

        if (!admin.getMemberRoleList().contains(MemberRole.ADMIN)) {
            throw new RuntimeException("관리자 권한이 없습니다");
        }
        //TODO: 지역 조회 후 예외처리 벨리데이션
        Gym newGym = convertToGym(createGymDTO);
        gymRepository.save(newGym);
        // 헬스장 등록 이미지 완료 !!!!
    }

    //헬스장 삭제!!
    @Override
    public void deleteGym(Long gymId, String adminUsername) {
        Member admin = memberRepository.getWithRoles(adminUsername)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다"));

        if (!admin.getMemberRoleList().contains(MemberRole.ADMIN)) {
            throw new RuntimeException("관리자 권한이 없습니다");
        }

        Gym gym = gymRepository.findByGymId(gymId).orElseThrow(() -> new RuntimeException("존재하지 않는 헬스장 입니다 "));

        List<String> imageNames = gym.getImageList().stream()
                .map(GymImage::getGymImageName)
                .collect(Collectors.toList());
        customFileUtil.deleteS3Files(imageNames);

        Long deleteGymId = gym.getId();
        gym.clearImageList();
        gymRepository.deleteById(deleteGymId);


        //TODO: 엔티티에 이미지 이름 남아있고  s3 에서만 삭제되게 0,1 delete flag 값


//GymImage 객체 -> 문자열 -> s3 삭제
    }

    //헬스장 정보 수정!!!
    @Override
    public void updateGym(Long gymId, CreateGymDTO createGymDTO, String adminUsername) {
        Member admin = memberRepository.getWithRoles(adminUsername)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다"));

        if (!admin.getMemberRoleList().contains(MemberRole.ADMIN)) {
            throw new RuntimeException("관리자 권한이 없습니다");
        }

        Gym gym = gymRepository.findByGymId(gymId).orElseThrow(() -> new RuntimeException("존재하지 않ㄴ느 헬스장ㅇ입니다 "));
        List<String> oldImageNames = gym.getImageList().stream()
                .map(GymImage::getGymImageName).collect(Collectors.toList());

        customFileUtil.deleteS3Files(oldImageNames);

        Gym updateGym = convertToGym(createGymDTO);
        updateGym.setId(gymId); // 아이디 설정
        gymRepository.save(updateGym);

    }


    //CreateGymDTO -> gym 엔티티
    private Gym convertToGym(CreateGymDTO createGymDTO) {

        Local local = localRepository.findByLocalName(createGymDTO.getLocal())
                .orElseThrow(() -> new RuntimeException("해당 지역이 존재하지 않습니다 "));
//local 지역 조회 후 시작 !!!
        List<String> imageNames = customFileUtil.uploadS3Files(createGymDTO.getFiles());

        Gym gym = Gym.builder()
                .gymName(createGymDTO.getGymName())
                .local(local)
                .address(createGymDTO.getAddress())
                .description(createGymDTO.getDescription())
                .dailyPrice(createGymDTO.getDailyPrice())
                .monthlyPrice(createGymDTO.getMonthlyPrice())
                .popular(createGymDTO.getPopular())

//이미지 이름을 gym 엔티티 imageList 에 저장
                .build();
        for (String imageName : imageNames) {
            gym.addImageString(imageName);
        } // 각각의 이미지 엔티티 로우기 이미지 개수 만큼 생김
        return gym;
    }
}