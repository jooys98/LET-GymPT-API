package com.example.gympt.domain.review.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ReviewRequestDTO {
    private Long bookingId;
    private Long gymId;
    private String content;
    private MultipartFile reviewImage;
    private Long trainerId;
    private Double rating;
}
