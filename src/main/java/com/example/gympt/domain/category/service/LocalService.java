package com.example.gympt.domain.category.service;

import com.example.gympt.domain.category.dto.LocalDTO;
import com.example.gympt.domain.category.dto.LocalParentDTO;
import com.example.gympt.domain.category.dto.LocalResponseDTO;
import com.example.gympt.domain.category.entity.Local;
import com.example.gympt.domain.gym.entity.Gym;
import com.example.gympt.domain.gym.entity.GymImage;

import java.util.List;
import java.util.stream.Collectors;

public interface LocalService {
    List<LocalResponseDTO> getLocalGymList(Long localId);

    List<LocalDTO> getAll();

    List<LocalDTO> getSubLocals(Long localId);

    default LocalResponseDTO convertToLocalDTO(Gym gym) {
        //gym -> LocalResponseDTO
        String image = gym.getImageList().stream().map(GymImage::getGymImageName).findFirst().orElse(null);
        return LocalResponseDTO.builder()
                .id(gym.getLocal().getId())
                .localName(gym.getLocal().getLocalName())
                .gymName(gym.getGymName())
                .address(gym.getAddress())
                .dailyPrice(gym.getDailyPrice())
                .monthlyPrice(gym.getMonthlyPrice())
                .likesCount(gym.getLikesCount())
                .popular(gym.getPopular())
                .gymImage(image)
                .build();
    }

    default LocalDTO convertToDTO(Local local) {
        return LocalDTO.builder()
                .id(local.getId())
                .localName(local.getLocalName())
                .build();
    }


    default LocalParentDTO convertToLocalParentDTO(Local local) {
        LocalParentDTO dto = new LocalParentDTO();
        dto.setId(local.getId());
        dto.setLocalName(local.getLocalName());
        if (local.getChildren() != null && !local.getChildren().isEmpty()) {
            dto.setChildren(local.getChildren().stream().map(this::convertToLocalParentDTO).collect(Collectors.toSet()));
        }

        return dto;
    }

    List<LocalParentDTO> getLocals(Long localId);
}