package com.example.gympt.domain.category.service;

import com.example.gympt.domain.category.dto.LocalDTO;
import com.example.gympt.domain.category.dto.LocalParentDTO;
import com.example.gympt.domain.category.dto.LocalResponseDTO;
import com.example.gympt.domain.category.entity.Local;
import com.example.gympt.domain.gym.dto.GymResponseDTO;
import com.example.gympt.domain.gym.entity.Gym;
import com.example.gympt.domain.gym.entity.GymImage;

import java.util.List;
import java.util.stream.Collectors;

public interface LocalService {
    List<GymResponseDTO> getLocalGymList(Long localId , String email);

    List<LocalDTO> getAll();

    List<LocalDTO> getSubLocals(Long localId);


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