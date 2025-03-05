package com.example.gympt.domain.gym.service;

import com.example.gympt.domain.category.entity.Local;
import com.example.gympt.domain.gym.dto.GymResponseDTO;
import com.example.gympt.domain.gym.dto.GymSearchRequestDTO;
import com.example.gympt.domain.gym.entity.Gym;
import com.example.gympt.domain.gym.entity.GymImage;
import com.example.gympt.dto.PageRequestDTO;
import com.example.gympt.dto.PageResponseDTO;

import java.util.ArrayList;
import java.util.List;

public interface GymService {
    PageResponseDTO<GymResponseDTO> getGyms(GymSearchRequestDTO gymSearchRequestDTO, PageRequestDTO pageRequestDTO, String email);


    GymResponseDTO getGymById(Long id, String email);
}
