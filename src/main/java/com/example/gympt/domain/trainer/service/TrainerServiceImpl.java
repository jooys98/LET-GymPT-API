package com.example.gympt.domain.trainer.service;

import com.example.gympt.domain.gym.entity.Gym;
import com.example.gympt.domain.gym.repository.GymRepository;
import com.example.gympt.domain.member.entity.Member;
import com.example.gympt.domain.member.enums.MemberRole;
import com.example.gympt.domain.member.repository.MemberRepository;
import com.example.gympt.domain.trainer.dto.TrainerRequestDTO;
import com.example.gympt.domain.trainer.dto.TrainerResponseDTO;
import com.example.gympt.domain.trainer.dto.TrainerSaveRequestDTO;
import com.example.gympt.domain.trainer.entity.TrainerSaveForm;
import com.example.gympt.domain.trainer.entity.TrainerSaveImage;
import com.example.gympt.domain.trainer.entity.Trainers;
import com.example.gympt.domain.trainer.repository.TrainerRepository;
import com.example.gympt.domain.trainer.repository.TrainerSaveFormRepository;
import com.example.gympt.dto.PageRequestDTO;
import com.example.gympt.dto.PageResponseDTO;
import com.example.gympt.util.s3.CustomFileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.gympt.domain.member.enums.MemberRole.PREPARATION_TRAINER;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TrainerServiceImpl implements TrainerService {


    private final MemberRepository memberRepository;
    private final GymRepository gymRepository;
    private final TrainerSaveFormRepository trainerSaveFormRepository;
    private final TrainerRepository trainerRepository;
    private final CustomFileUtil customFileUtil;


    @Override
//트레이너 데뷔 신청!!
    public void saveTrainer(TrainerSaveRequestDTO trainerSaveRequestDTO) {
        Member member = memberRepository.getWithRoles(trainerSaveRequestDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다"));

        if (!member.getMemberRoleList().contains(MemberRole.PREPARATION_TRAINER)) {
            throw new RuntimeException("트레이너 신청 회원이 아닙니다");
        }
        Gym gym = gymRepository.findByGymId(trainerSaveRequestDTO.getGymId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 헬스장입니다"));

        List<String> images = customFileUtil.uploadS3Files(trainerSaveRequestDTO.getFiles());
        //trainerSaveRequestDTO 이미지 이름 문자열 리스트 가져와서 s3 업로드 후 이미지 이름 문자열 반환

//트레이너 신청 테이블에 저장됨!
        TrainerSaveForm trainer = new TrainerSaveForm();
        trainer.setMember(member);
        trainer.setGym(gym);
        trainer.setName(trainerSaveRequestDTO.getName());
        trainer.setAge(trainerSaveRequestDTO.getAge());
        trainer.setGender(trainerSaveRequestDTO.getGender());
        trainer.setIntroduction(trainerSaveRequestDTO.getIntroduction());

        for(String image : images) {
            trainer.addImageString(image);
        }
        //trainerSaveRequestDTO 이미지 를 TrainerSaveImage 로 바꿔주는 메서드
        trainerSaveFormRepository.save(trainer);
    }


    //트레이너 조건 별 정보 조회!
    @Override
    public PageResponseDTO<TrainerResponseDTO> getTrainers(TrainerRequestDTO trainerRequestDTO, PageRequestDTO pageRequestDTO) {
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize());
        List<TrainerResponseDTO> trainerList = trainerRepository.findTrainers(trainerRequestDTO, pageable)
                .stream().map(this::trainerEntityToDTO).collect(Collectors.toList());
        Long totalCount = trainerRepository.countTrainers(trainerRequestDTO);
        return new PageResponseDTO<>(trainerList, pageRequestDTO, totalCount);
    }

    @Override
    public TrainerResponseDTO getTrainerById(Long id) {
      Trainers trainers = trainerRepository.findById(id).orElseThrow(()->new RuntimeException("존재하지 않는 트레이너 입니다"));
      return trainerEntityToDTO(trainers);
    }


}
