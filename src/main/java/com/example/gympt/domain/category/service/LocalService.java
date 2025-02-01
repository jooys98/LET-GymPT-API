package com.example.gympt.domain.category.service;

import com.example.gympt.domain.category.dto.LocalDTO;
import com.example.gympt.domain.category.dto.LocalResponseDTO;

import java.util.List;

public interface LocalService {
    List<LocalResponseDTO> getLocalGymList(Long localId);

    List<LocalDTO> getAll();
}
