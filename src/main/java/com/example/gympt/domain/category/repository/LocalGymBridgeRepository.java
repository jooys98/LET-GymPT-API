package com.example.gympt.domain.category.repository;

import com.example.gympt.domain.category.entity.LocalGymBridge;
import com.example.gympt.domain.gym.entity.Gym;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LocalGymBridgeRepository extends JpaRepository<LocalGymBridge, Long> {

    @Modifying
    @Query("delete from LocalGymBridge lg where lg.gym = :gym")
    void deleteByGym(@Param("gym") Gym gym);
}

