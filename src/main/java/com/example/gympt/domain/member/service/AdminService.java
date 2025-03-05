package com.example.gympt.domain.member.service;

import com.example.gympt.domain.category.dto.LocalDTO;
import com.example.gympt.domain.category.entity.Local;
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
    void approveTrainer(String trainerEmail);

    List<TrainerSaveFormDTO> getPreparationTrainers();

    default TrainerSaveFormDTO converTodto(TrainerSaveForm trainerSaveForm) {
        List<String> imageNames = trainerSaveForm.getImageList().stream()
                .map(TrainerSaveImage::getTrainerSaveImageName)
                .toList();
        return TrainerSaveFormDTO.builder()
                .id(trainerSaveForm.getId())
                .name(trainerSaveForm.getName())
                .age(trainerSaveForm.getAge())
                .introduction(trainerSaveForm.getIntroduction())
                .gender(trainerSaveForm.getGender().toString())
                .gymName(trainerSaveForm.getGym().getGymName())
                .imagePathList(imageNames)
                .build();
    }


    void createGym(CreateGymDTO createGymDTO);

    void deleteGym(Long gymId);

    void updateGym(Long gymId, CreateGymDTO createGymDTO);

    List<LocalDTO> localList();

    void removeLocal(Long localId);

    default LocalDTO entityToDTO(Local local){
            return LocalDTO.builder()
                    .id(local.getId())
                    .localName(local.getLocalName())
                    .build();
        }
    }

