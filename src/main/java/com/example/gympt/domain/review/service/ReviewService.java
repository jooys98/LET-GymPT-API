package com.example.gympt.domain.review.service;

import com.example.gympt.domain.gym.entity.Gym;
import com.example.gympt.domain.member.entity.Member;
import com.example.gympt.domain.review.dto.ReviewRequestDTO;
import com.example.gympt.domain.review.dto.ReviewResponseDTO;
import com.example.gympt.domain.review.entity.Review;
import com.example.gympt.domain.trainer.entity.Trainers;

import java.time.LocalDateTime;
import java.util.List;

public interface ReviewService {
    void createReview(String email, ReviewRequestDTO review);

    List<ReviewResponseDTO> getMyReviewList(String email);

    List<ReviewResponseDTO> getGymReviews(Long gymId);

    Long deleteReview(String email, Long reviewId);


    default Review dtoToEntity(ReviewRequestDTO reviewRequestDTO, Member member, Gym gym, String reviewImage, Trainers trainers) {
        return Review.builder()
                .content(reviewRequestDTO.getContent())
                .member(member)
                .gym(gym)
                .createdAt(LocalDateTime.now())
                .trainers(trainers)
                .reviewImage(reviewImage)
                .rating(reviewRequestDTO.getRating())
                .build();
    }

    default ReviewResponseDTO EntityToDTO(Review review) {
        return ReviewResponseDTO.builder()
                .name(review.getMember().getName())
                .gymName(review.getGym().getGymName())
                .gymId(review.getGym().getId())
                .trainerId(review.getTrainers() == null ? null : review.getTrainers().getId())
                .content(review.getContent())
                .reviewImage(review.getReviewImage())
                .rating(review.getRating())
                .createdAt(review.getCreatedAt())
                .trainerName(review.getTrainers() == null ? null : review.getTrainers().getTrainerName())
                .build();
    }

    void deleteByGym(Gym gym);
}
