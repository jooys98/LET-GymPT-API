package com.example.gympt.domain.review.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Builder
public class ReviewRequestDTO {
  private Long gymId;
    private String gymName;
    private String content;
    private String name;//작성자
    private MultipartFile reviewImage;
    private String trainerName;
    private Double rating;
}
