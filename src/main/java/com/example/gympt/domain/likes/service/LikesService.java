package com.example.gympt.domain.likes.service;

import com.example.gympt.domain.gym.entity.Gym;
import com.example.gympt.domain.likes.dto.LikesGymDTO;
import com.example.gympt.domain.likes.dto.LikesTrainersDTO;
import com.example.gympt.domain.likes.entity.LikesGym;

import java.util.List;

public interface LikesService {
    Boolean toggleGymLikes(String email, Long gymId);

    List<LikesGymDTO> getLikesGymList(String email);

    Boolean toggleTrainerLikes(String email, Long trainerId);

    List<LikesTrainersDTO> getLikesTrainerList(String email);


}
