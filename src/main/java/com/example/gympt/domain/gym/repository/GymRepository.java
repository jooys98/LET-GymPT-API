package com.example.gympt.domain.gym.repository;

import com.example.gympt.domain.gym.entity.Gym;
import com.example.gympt.domain.gym.repository.queryDsl.GymRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface GymRepository extends JpaRepository<Gym, Long> , GymRepositoryCustom {
    @Query("select g from Gym g where g.id = :id")
    Optional<Gym> findByGymId(@Param("id") Long gymId);

    @Query("select g from Gym g where g.gymName = :name")
    Optional<Gym> findByGymName(@Param("name") String gymName);

    @Query("select g from Gym g where g.id = :id")
    Boolean existsGymId(@Param("id") Long gymId);

    @Query("select g from Gym g where g.local = :id")
    List<Gym> findByLocalId(@Param("id") Long id);

    @Query("select case when count(g) > 0 then true else false end from Gym g where g.local.id = :localId")
    boolean existsByLocal(@Param("localId") Long localId);


}
