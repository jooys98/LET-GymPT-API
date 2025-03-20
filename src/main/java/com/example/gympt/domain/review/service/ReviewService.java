package com.example.gympt.domain.review.service;

import com.example.gympt.domain.gym.entity.Gym;
import com.example.gympt.domain.gym.entity.GymImage;
import com.example.gympt.domain.member.entity.Member;
import com.example.gympt.domain.review.dto.ReviewRequestDTO;
import com.example.gympt.domain.review.dto.ReviewResponseDTO;
import com.example.gympt.domain.review.entity.Review;
import com.example.gympt.domain.trainer.dto.TrainerResponseDTO;
import com.example.gympt.domain.trainer.entity.Trainers;

import java.time.LocalDateTime;
import java.util.List;

public interface ReviewService {
    void createReview(String email, ReviewRequestDTO review);

    List<ReviewResponseDTO> getMyReviewList(String email);

    List<ReviewResponseDTO> getGymReviews(Long gymId);

    Long deleteReview(String email, Long reviewId);
    List<ReviewResponseDTO> getReviewListByTrainer(String email);





    void deleteByGym(Gym gym);


    List<ReviewResponseDTO> getTrainerReviews(Long trainerId);

}
