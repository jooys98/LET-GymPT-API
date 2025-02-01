package com.example.gympt.domain.category.dto;

import com.example.gympt.domain.gym.dto.GymResponseDTO;
import com.example.gympt.domain.gym.entity.Gym;
import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor
@ToString
public class LocalResponseDTO {
    private Long id;
    private String localName;
    private List<GymResponseDTO> gyms;
}
