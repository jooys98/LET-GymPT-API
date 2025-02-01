package com.example.gympt.domain.gym.dto;

import com.example.gympt.domain.gym.enums.Popular;
import com.example.gympt.domain.review.entity.Review;
import com.example.gympt.domain.trainer.entity.Trainers;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@ToString
public class GymResponseDTO {
    private Long id;
    private String localName;
    private String gymName;
    private String address;
    private String description;
    private Long dailyPrice;
    private Long monthlyPrice;
    private int likesCount;
    private Popular popular;
    private List<Review> reviewList;
    private List<Trainers> trainers;


    // 파일 업로드한 url 응답값
    @Builder.Default
    private List<String> uploadFileNames = new ArrayList<>();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedAt;
}
