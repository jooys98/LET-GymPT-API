package com.example.gympt.domain.member.service;

import com.example.gympt.domain.category.dto.LocalDTO;
import com.example.gympt.domain.category.entity.Local;
import com.example.gympt.domain.category.entity.LocalGymBridge;
import com.example.gympt.domain.category.repository.LocalGymBridgeRepository;
import com.example.gympt.domain.category.repository.LocalRepository;
import com.example.gympt.domain.gym.entity.Gym;
import com.example.gympt.domain.gym.entity.GymImage;
import com.example.gympt.domain.gym.repository.GymRepository;
import com.example.gympt.domain.member.dto.CreateGymDTO;
import com.example.gympt.domain.member.entity.Member;
import com.example.gympt.domain.member.enums.MemberRole;
import com.example.gympt.domain.member.repository.MemberRepository;
import com.example.gympt.domain.review.repository.ReviewRepository;
import com.example.gympt.domain.review.service.ReviewService;
import com.example.gympt.domain.trainer.dto.TrainerSaveFormDTO;
import com.example.gympt.domain.trainer.entity.TrainerSaveForm;
import com.example.gympt.domain.trainer.entity.TrainerSaveImage;
import com.example.gympt.domain.trainer.entity.Trainers;
import com.example.gympt.domain.trainer.enums.Gender;
import com.example.gympt.domain.trainer.repository.TrainerRepository;
import com.example.gympt.domain.trainer.repository.TrainerSaveFormRepository;
import com.example.gympt.domain.trainer.service.TrainerService;
import com.example.gympt.exception.CustomDoesntExist;
import com.example.gympt.exception.CustomNotAccessHandler;
import com.example.gympt.notification.service.NotificationService;
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
    private final NotificationService notificationService;
    private final LocalGymBridgeRepository localGymBridgeRepository;
    private final ReviewService reviewService;
    private final TrainerService trainerService;

    @Transactional
    @Override
    //트레이너 권한 바꿔주기 + 트레이너 테이블에 새로운 트레이너 추가
    public void approveTrainer(String trainerEmail) {

        // 트레이너 승인 대상 확인
        Member trainer = getMemberByEmail(trainerEmail);
        if (!trainer.getMemberRoleList().contains(MemberRole.PREPARATION_TRAINER)) {
            throw new CustomDoesntExist("승인 대상이 아닙니다");
        }

        // PREPARATION_TRAINER -> TRAINER 권한 변경
        trainer.getMemberRoleList().remove(MemberRole.PREPARATION_TRAINER);
        trainer.getMemberRoleList().add(MemberRole.TRAINER);
        memberRepository.save(trainer); // 권한 바꿔주기
        saveTrainer(trainerEmail);
        notificationService.sendTrainerApproval(trainerEmail);
    }

    @Transactional
    public void saveTrainer(String trainerEmail) {

        try {
            TrainerSaveForm trainerSaveForm = getPreparationTrainer(trainerEmail);
            Gym gym = getGym(trainerSaveForm.getGym().getId());

            String profileImage = trainerSaveForm.getProfileImage();
            List<TrainerSaveImage> images = trainerSaveForm.getImageList();
            List<String> imageList = images.stream().map(TrainerSaveImage::getTrainerSaveImageName).toList();

            Trainers trainer = Trainers.builder()
                    .trainerName(trainerSaveForm.getName())
                    .age(trainerSaveForm.getAge())
                    .introduction(trainerSaveForm.getIntroduction())
                    .member(trainerSaveForm.getMember())
                    .gym(gym)
                    .profileImage(profileImage)
                    .local(trainerSaveForm.getGym().getLocal())
                    .build();
            trainer.addGender(trainerSaveForm.getGender());
            gym.addTrainer(trainer);
            gymRepository.save(gym);

            for (String imageName : imageList) {
                trainer.addImageString(imageName);
            }


            log.info("저장 트레이너 email", trainer.getTrainerName());
            trainerSaveFormRepository.delete(trainerSaveForm);
            trainerRepository.save(trainer);

        } catch (Exception e) {
            throw new RuntimeException("트레이너 저장 중  오류 발생 " + e.getMessage(), e);
        }

    }


//트레이너 회원가입을 따로 안만든 이유 :
//카카오 소셜 로그인도 있어서 같이 연동하려면 너무 복잡해지기 때문
//(kakao) member -> trainer 형식으로 구현
//트레이너 신청시 TrainerSaveForm 에 insert -> admin 의 허용시 Trainers 에 트레이너 정보가 그대로 insert 된다


    @Override
    public List<TrainerSaveFormDTO> getPreparationTrainers() {
        List<TrainerSaveFormDTO> trainerSaveFormDTOList = trainerSaveFormRepository.findAll()
                .stream().map(this::converTodto).toList();
        return trainerSaveFormDTOList;

    }


    @Override
    public void createGym(CreateGymDTO createGymDTO) {
        Local local = getLocalId(createGymDTO.getLocalId());
        Gym newGym = convertToGym(createGymDTO);
        gymRepository.save(newGym);
        if (createGymDTO.getLocalId() != null) {
            localGymBridgeRepository.save(LocalGymBridge.from(local, newGym));
        }
    }

    //헬스장 삭제!!
    @Override
    public Long deleteGym(Long gymId) {
        Gym gym = getGym(gymId);

        List<String> imageNames = gym.getImageList().stream()
                .map(GymImage::getGymImageName)
                .toList();
        customFileUtil.deleteS3Files(imageNames);

        gym.clearImageList(); //객체 삭제
        gymRepository.delete(gym); // 엔티티 삭제
        localGymBridgeRepository.deleteByGym(gym);
        reviewService.deleteByGym(gym);
        trainerService.changeByGym(gym);
//GymImage 객체 -> 문자열 -> s3 삭제
        return gymId;
    }


    //헬스장 정보 수정!!!
    @Transactional
    @Override
    public Long updateGym(Long gymId, CreateGymDTO createGymDTO) {

        Gym gym = getGym(gymId);
        Local local = getLocal(createGymDTO.getLocalId());

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
        return gym.getId();
    }


    //카테고리 삭제
    @Override
    public Long removeLocal(Long localId) {
        // 연관관계가 있는 카테고리라면 삭제 불가능
        if (gymRepository.existsByLocal(localId)) {
            throw new IllegalStateException("지역에 해당하는 헬스장이 있어 삭제가 불가능합니다.");
        }
        localRepository.deleteById(localId);
        return localId;
    }


    //CreateGymDTO -> gym 엔티티
    private Gym convertToGym(CreateGymDTO createGymDTO) {

        Local local = getLocal(createGymDTO.getLocalId());
//local 지역 조회 후 해당 local 정보도 함께 entity 에 담기
        List<String> imageNames;

        // 엑셀에서 온 경우 (uploadFileNames가 있고 files가 없는 경우)
        if ((createGymDTO.getFiles() == null || createGymDTO.getFiles().isEmpty())
                && createGymDTO.getUploadFileNames() != null && !createGymDTO.getUploadFileNames().isEmpty()) {
            // uploadFileNames에 있는 경로로부터 이미지를 처리
            // 예: S3에서 이미 존재하는 이미지를 참조하거나, 로컬에서 파일을 찾아 S3에 업로드
            imageNames = customFileUtil.uploadImagePathS3Files(createGymDTO.getUploadFileNames());
        } else {
            // 웹 폼에서 온 경우
            imageNames = customFileUtil.uploadS3Files(createGymDTO.getFiles());
        }


        Gym gym = Gym.builder()
                .gymName(createGymDTO.getGymName())
                .local(local)
                .address(createGymDTO.getAddress())
                .description(createGymDTO.getDescription())
                .dailyPrice(createGymDTO.getDailyPrice())
                .monthlyPrice(createGymDTO.getMonthlyPrice())
                .info(createGymDTO.getInfo())
                .build();

        gym.addPopular(createGymDTO.getPopular());
        //이미지 이름을 gym 엔티티 imageList 에 저장
        for (String imageName : imageNames) {
            gym.addImageString(imageName);
        } // 각각의 이미지 엔티티 로우기 이미지 개수 만큼 생김
        return gym;
    }



    private Gym getGym(Long gymId) {
        return gymRepository.findByGymId(gymId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 헬스장 입니다 "));
    }

    private Local getLocal(Long localId) {
        return localRepository.findById(localId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 지역입니다"));
    }


    private Local getLocalId(Long localId) {
        return localRepository.findById(localId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 지역입니다"));
    }

    private Member getMemberByEmail(String email) {
        return memberRepository.getWithRoles(email)
                .orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다"));
    }

    private TrainerSaveForm getPreparationTrainer(String trainerEmail) {
        return trainerSaveFormRepository.findByEmail(trainerEmail)
                .orElseThrow(() -> new EntityNotFoundException("신청 내역이 없습니다"));
    }
}