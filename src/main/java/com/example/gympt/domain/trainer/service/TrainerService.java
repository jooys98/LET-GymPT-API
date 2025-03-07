package com.example.gympt.domain.trainer.service;

import com.example.gympt.domain.gym.entity.Gym;
import com.example.gympt.domain.member.dto.MemberResponseDTO;
import com.example.gympt.domain.member.entity.Member;
import com.example.gympt.domain.reverseAuction.dto.TrainerAuctionRequestDTO;
import com.example.gympt.domain.trainer.dto.TrainerRequestDTO;
import com.example.gympt.domain.trainer.dto.TrainerResponseDTO;
import com.example.gympt.domain.trainer.dto.TrainerSaveRequestDTO;
import com.example.gympt.domain.trainer.entity.TrainerImage;
import com.example.gympt.domain.trainer.entity.TrainerSaveForm;
import com.example.gympt.domain.trainer.entity.Trainers;
import com.example.gympt.dto.PageRequestDTO;
import com.example.gympt.dto.PageResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;


public interface TrainerService {

    void saveTrainer(String TrainerEmail, TrainerSaveRequestDTO trainerSaveRequestDTO);

    PageResponseDTO<TrainerResponseDTO> getTrainers(TrainerRequestDTO trainerRequestDTO, PageRequestDTO pageRequestDTO, String email);


    //트레이너 정보 조회시 쓰이는 dto 변환 메서드
    default TrainerResponseDTO trainerEntityToDTO(Trainers trainers, boolean likes) {
        List<String> imageNames = trainers.getImageList().stream()
                .map(TrainerImage::getTrainerImageName)
                .toList();
//Trainers 엔티티 속 이미지 리스트 객체를  가져와서 문자열 리스트로 만들어주기
        TrainerResponseDTO responseDTO = TrainerResponseDTO.builder()
                .id(trainers.getId())
                .email(trainers.getMember().getEmail())
                .age(trainers.getAge())
                .name(trainers.getMember().getName())
                .gender(trainers.getGender().toString())
                .introduction(trainers.getIntroduction())
                .gymId(trainers.getGym().getId())
                .localId(trainers.getLocal().getId())
                .gymName(trainers.getGym().getGymName())
                .local(trainers.getLocal().getLocalName())
                .likesCount(trainers.getLikesCount())
                .imageList(imageNames)
                .likes(likes)
                .build();
        return responseDTO;

    }


    TrainerResponseDTO getTrainerById(Long id, String email);

    void applyAuction(String TrainerEmail, TrainerAuctionRequestDTO trainerAuctionRequestDTO);

    Long changePrice(Long auctionRequestId, String trainerEmail, Long updatePrice);

    void changeByGym(Gym gym);

    List<TrainerResponseDTO> getTrainerByGymId(Long id, String email);

    Long updateTrainer(String email, TrainerSaveRequestDTO trainerSaveRequestDTO);

    TrainerResponseDTO getTrainerDetail(String email);
}
