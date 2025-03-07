package com.example.gympt.domain.review.dto;

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
    private String content;
    private String name;//작성자
    private LocalDateTime createdAt;
    private String reviewImage;
    private Long trainerId;
    private String trainerName;
    private Double rating;
    private boolean active;
}
