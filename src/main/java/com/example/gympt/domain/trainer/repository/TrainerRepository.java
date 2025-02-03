package com.example.gympt.domain.trainer.repository;

import com.example.gympt.domain.trainer.entity.TrainerSaveForm;
import com.example.gympt.domain.trainer.entity.Trainers;
import com.example.gympt.domain.trainer.repository.querydsl.TrainerRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TrainerRepository extends JpaRepository<Trainers,Long>, TrainerRepositoryCustom {

    @Query("select t from Trainers t join t.member m where m.email = :email")
    Optional<TrainerSaveForm> findByEmail(@Param("email") String email);

    @Query("select t from Trainers t join t.member m where m.email = :email")
    Optional<Trainers> findByTrainerEmail(@Param("email") String email);

}
