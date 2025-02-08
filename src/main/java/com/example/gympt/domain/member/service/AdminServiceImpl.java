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
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
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
        Member admin = getMemberRoles(adminUsername);

        if (!admin.getMemberRoleList().contains(MemberRole.ADMIN)) {
            throw new RuntimeException("관리자 권한이 없습니다");
        }

        // 트레이너 승인 대상 확인
        Member trainer = getMemberByEmail(trainerEmail);
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
        TrainerSaveForm trainerSaveForm = trainerSaveFormRepository.findByEmail(trainerEmail)
                .orElseThrow(() -> new EntityNotFoundException("신청 내역이 없습니다"));

        Gym gym = getGym(trainerSaveForm.getGym().getId());

        List<TrainerSaveImage> images = trainerSaveForm.getImageList();

        Trainers trainer = Trainers.builder()
                .trainerName(trainerSaveForm.getName())
                .age(trainerSaveForm.getAge())
                .introduction(trainerSaveForm.getIntroduction())
                .gender(Gender.valueOf(trainerSaveForm.getGender()))
                .member(trainerSaveForm.getMember())
                .gym(gym)
                .local(trainerSaveForm.getGym().getLocal())
                .build();
        try {
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
        } catch (Exception e) {
            e.getMessage();
        }
    }

//트레이너 회원가입을 따로 안만든 이유 :
//카카오 소셜 로그인도 있어서 같이 연동하려면 너무 복잡해지기 때문에
//(kakao) member -> trainer 형식으로 구현
//트레이너 신청시 TrainerSaveForm 에 insert -> admin 의 허용시 Trainers 에 트레이너 정보가 그대로 insert 된다


    //트레이너 신청 목록 리스트로 보기 !!!!
    @Override
    public List<TrainerSaveFormDTO> getPreparationTrainers(String adminUsername) {
        Member admin = getMemberRoles(adminUsername);

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
        Member admin = getMemberRoles(adminUsername);
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
        Member admin = getMemberRoles(adminUsername);

        if (!admin.getMemberRoleList().contains(MemberRole.ADMIN)) {
            throw new RuntimeException("관리자 권한이 없습니다");
        }

        Gym gym = getGym(gymId);

        List<String> imageNames = gym.getImageList().stream()
                .map(GymImage::getGymImageName)
                .toList();
        customFileUtil.deleteS3Files(imageNames);

        gym.clearImageList();
        gymRepository.delete(gym);


//GymImage 객체 -> 문자열 -> s3 삭제
    }


    //헬스장 정보 수정!!!
    @Transactional
    @Override
    public void updateGym(Long gymId, CreateGymDTO createGymDTO, String adminUsername) {
        Member admin = getMemberRoles(adminUsername);

        if (!admin.getMemberRoleList().contains(MemberRole.ADMIN)) {
            throw new RuntimeException("관리자 권한이 없습니다");
        }

        Gym gym = getGym(gymId);
        Local local = getLocal(createGymDTO.getLocal());

        List<String> oldImageNames = gym.getImageList().stream()
                .map(GymImage::getGymImageName).toList();
        //삭제할 기존 이미지들

        List<String> updateImages = customFileUtil.uploadS3Files(createGymDTO.getFiles());
        //새로 업로드 할 이미지들을 s3에 업로드

        List<String> uploadedFileNames = createGymDTO.getUploadFileNames();
//화면에 유지되는 이미지들
        if (updateImages != null && !updateImages.isEmpty()) {
            //s3에 새로 업데이트 한 파일이 있다면 ?
            uploadedFileNames.addAll(updateImages);
            //화면 유지 이미지 리스트에 s3 이미지 저장 리턴이름들 추가
        }

        if (oldImageNames != null && !oldImageNames.isEmpty()) {
            //삭제할 이미지가 있다면?
            List<String> removeFiles = oldImageNames.stream()
                    .filter(fileName -> !uploadedFileNames.contains(fileName)).toList();
            //화면유지+ 업로드 이미지들 중 삭제할 이미지들을 찾아준다

            try {
                customFileUtil.deleteS3Files(removeFiles);
                //s3이미지 삭제
            } catch (Exception e) {
                log.error("파일 삭제 에러 : {}", e.getMessage());
                throw new RuntimeException("파일 삭제 에러");
            }
        }
        gym.clearImageList();
        //db 이미지 삭제

        if (uploadedFileNames != null && !uploadedFileNames.isEmpty()) {
            uploadedFileNames.forEach(gym::addImageString);
            //화면유지+ 업로드 이미지들을 db 에 저장한다
        }
        gym.updateGym(createGymDTO.getGymName());
        gym.updateAddress(createGymDTO.getAddress());
        gym.updateLocal(local);
        gym.updateDescription(createGymDTO.getDescription());
        gym.updateDailyPrice(createGymDTO.getDailyPrice());
        gym.updateMonthlyPrice(createGymDTO.getMonthlyPrice());
    //나머지 정보들도 수정한다
    }


    //CreateGymDTO -> gym 엔티티
    private Gym convertToGym(CreateGymDTO createGymDTO) {

        Local local = getLocal(createGymDTO.getLocal());
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
                .build();
        //이미지 이름을 gym 엔티티 imageList 에 저장
        for (String imageName : imageNames) {
            gym.addImageString(imageName);
        } // 각각의 이미지 엔티티 로우기 이미지 개수 만큼 생김
        return gym;
    }


    private Member getMemberRoles(String email) {
        return memberRepository.getWithRoles(email)
                .orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다"));
    }

    private Gym getGym(Long gymId) {
        return gymRepository.findByGymId(gymId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 헬스장 입니다 "));
    }

    private Local getLocal(String local) {
        return localRepository.findByLocalName(local).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 지역입니다"));
    }

    private Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다"));
    }
}