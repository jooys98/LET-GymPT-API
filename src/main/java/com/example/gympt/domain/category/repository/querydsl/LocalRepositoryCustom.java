package com.example.gympt.domain.category.repository.querydsl;

import com.example.gympt.domain.category.entity.Local;
import com.example.gympt.domain.gym.entity.Gym;

import java.util.List;

public interface LocalRepositoryCustom {

List<Local> findAllLocal();

List<Local> findByLocalId(Long localId);

List<Gym> findGymByLocalId(Long localId);

}
