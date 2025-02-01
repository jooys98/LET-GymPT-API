package com.example.gympt.domain.member.service;

import com.example.gympt.domain.gym.entity.Gym;
import com.example.gympt.domain.member.dto.CreateGymDTO;
import com.example.gympt.domain.trainer.dto.TrainerSaveFormDTO;
import com.example.gympt.domain.trainer.entity.TrainerSaveForm;
import com.example.gympt.domain.trainer.entity.TrainerSaveImage;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public interface AdminService {
    void approveTrainer(String trainerEmail, String adminUsername);

    List<TrainerSaveFormDTO> getPreparationTrainers(String adminUsername);

default TrainerSaveFormDTO converTodto (TrainerSaveForm trainerSaveForm) {
    List<String> imageNames = trainerSaveForm.getImageList().stream()
            .map(TrainerSaveImage::getTrainerSaveImageName)
            .toList();
 return TrainerSaveFormDTO.builder()
         .id(trainerSaveForm.getId())
         .name(trainerSaveForm.getName())
         .age(trainerSaveForm.getAge())
         .introduction(trainerSaveForm.getIntroduction())
         .gender(trainerSaveForm.getGender())
         .gymName(trainerSaveForm.getGym().getGymName())
         .imagePathList(imageNames)
         .build();
}



    void createGym(CreateGymDTO createGymDTO, String adminUsername);

    void deleteGym(Long gymId, String adminUsername);

    void updateGym(Long gymId, CreateGymDTO createGymDTO, String adminUsername);
}
