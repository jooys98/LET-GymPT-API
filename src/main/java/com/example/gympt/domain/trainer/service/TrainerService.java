package com.example.gympt.domain.trainer.service;

import com.example.gympt.domain.trainer.dto.TrainerRequestDTO;
import com.example.gympt.domain.trainer.dto.TrainerResponseDTO;
import com.example.gympt.domain.trainer.dto.TrainerSaveRequestDTO;
import com.example.gympt.domain.trainer.entity.TrainerImage;
import com.example.gympt.domain.trainer.entity.Trainers;
import com.example.gympt.dto.PageRequestDTO;
import com.example.gympt.dto.PageResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;


public interface TrainerService {

    void saveTrainer(TrainerSaveRequestDTO trainerSaveRequestDTO);

    PageResponseDTO<TrainerResponseDTO> getTrainers(TrainerRequestDTO trainerRequestDTO, PageRequestDTO pageRequestDTO);


    //트레이너 정보 조회시 쓰이는 dto 변환 메서드
default TrainerResponseDTO trainerEntityToDTO(Trainers trainers) {
    List<String> imageNames = trainers.getImageList().stream()
            .map(TrainerImage::getTrainerImageName)
            .toList();

    TrainerResponseDTO responseDTO = TrainerResponseDTO.builder()
            .id(trainers.getId())
            .email(trainers.getMember().getEmail())
            .age(trainers.getAge())
            .name(trainers.getMember().getName())
            .gender(trainers.getGender().toString())
            .introduction(trainers.getIntroduction())
            .gymName(trainers.getGym().toString())
            .local(trainers.getLocal().toString())
            .likesCount(trainers.getLikesCount())
            .uploadFileNames(imageNames)
            .build();
    return responseDTO;

}


}
