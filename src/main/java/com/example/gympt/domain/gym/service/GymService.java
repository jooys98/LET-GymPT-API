package com.example.gympt.domain.gym.service;

import com.example.gympt.domain.gym.dto.GymResponseDTO;
import com.example.gympt.domain.gym.dto.GymSearchRequestDTO;
import com.example.gympt.domain.gym.entity.Gym;
import com.example.gympt.domain.gym.entity.GymImage;
import com.example.gympt.dto.PageRequestDTO;
import com.example.gympt.dto.PageResponseDTO;

import java.util.ArrayList;
import java.util.List;

public interface GymService {
    PageResponseDTO<GymResponseDTO> getGyms(GymSearchRequestDTO gymSearchRequestDTO , PageRequestDTO pageRequestDTO);

    default GymResponseDTO entityToDTO(Gym gym) {
        List<String> imageNames = gym.getImageList().stream()
                .map(GymImage::getGymImageName)
                .toList();

        GymResponseDTO responseDTO = GymResponseDTO.builder()
                .id(gym.getId())
                .gymName(gym.getGymName())
                .localName(gym.getGymName())
                .address(gym.getAddress())
                .description(gym.getDescription())
                .dailyPrice(gym.getDailyPrice())
                .monthlyPrice(gym.getMonthlyPrice())
                .likesCount(gym.getLikesCount())
                .popular(gym.getPopular())
                .uploadFileNames(imageNames)
                .build();
        return responseDTO;
    }
}
