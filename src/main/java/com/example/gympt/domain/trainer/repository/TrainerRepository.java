package com.example.gympt.domain.trainer.repository;

import com.example.gympt.domain.trainer.entity.TrainerSaveForm;
import com.example.gympt.domain.trainer.entity.Trainers;
import com.example.gympt.domain.trainer.repository.querydsl.TrainerRepositoryCustom;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TrainerRepository extends JpaRepository<Trainers, Long>, TrainerRepositoryCustom {


    @EntityGraph(attributePaths = {"memberRoleList"})
    @Query("select t from Trainers t join t.member m where m.email = :email")
    Optional<Trainers> findByTrainerEmail(@Param("email") String email);

    @Query("select t from Trainers t join t.member m where m.name = :trainerName")
    Optional<Trainers> findByName(@Param("trainerName") String trainerName);

    @Query("select t from Trainers t join t.gym g where g.id =:id")
    List<Trainers> findByGymId(@Param(("id")) Long id);
}
