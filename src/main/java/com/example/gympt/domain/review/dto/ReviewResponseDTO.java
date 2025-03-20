package com.example.gympt.domain.review.dto;

import com.example.gympt.domain.gym.entity.GymImage;
import com.example.gympt.domain.review.entity.Review;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Builder
public class ReviewResponseDTO {

    private Long gymId;
    private String gymName;
    private String gymImage;
    private String content;
    private String name;//작성자
    private LocalDateTime createdAt;
    private String reviewImage;
    private Long trainerId;
    private String trainerName;
    private Double rating;
    private boolean active;

    public static ReviewResponseDTO from(Review review) {
        return ReviewResponseDTO.builder()
                .name(review.getMember().getName())
                .gymName(review.getGym().getGymName())
                .gymImage(review.getGym().getImageList().stream().map(GymImage::getGymImageName).findFirst().toString())
                .gymId(review.getGym().getId())
                .trainerId(review.getTrainers() == null ? null : review.getTrainers().getId())
                .content(review.getContent())
                .reviewImage(review.getReviewImage())
                .rating(review.getRating())
                .createdAt(review.getCreatedAt())
                .trainerName(review.getTrainers() == null ? null : review.getTrainers().getTrainerName())
                .build();
    }
}
