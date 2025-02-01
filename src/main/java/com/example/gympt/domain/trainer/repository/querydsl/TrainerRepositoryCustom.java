package com.example.gympt.domain.trainer.repository.querydsl;

import com.example.gympt.domain.trainer.dto.TrainerRequestDTO;
import com.example.gympt.domain.trainer.entity.Trainers;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TrainerRepositoryCustom {

    List<Trainers> findTrainers(TrainerRequestDTO trainerRequestDTO, Pageable pageable);

    Long countTrainers(TrainerRequestDTO trainerRequestDTO);
}
