package com.example.gympt.domain.gym.repository.queryDsl;

import com.example.gympt.domain.gym.dto.GymSearchRequestDTO;
import com.example.gympt.domain.gym.entity.Gym;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GymRepositoryCustom {
List<Gym> findByGym(GymSearchRequestDTO gymSearchRequestDTO, Pageable pageable);

Long countByGym(GymSearchRequestDTO gymSearchRequestDTO);
}
