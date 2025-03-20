package com.example.gympt.domain.gym.dto;

import com.example.gympt.domain.gym.enums.Popular;
import com.example.gympt.domain.review.dto.ReviewResponseDTO;
import com.example.gympt.domain.trainer.entity.Trainers;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    private String parentLocal; // 관악구
    private String childrenLocal; // 봉천동
    private String localName; // 지하철역
    private String gymName;
    private String address;
    private String description;
    private Long dailyPrice;
    private Long monthlyPrice;
    private int likesCount;
    private Popular popular;
    private boolean likes;
    private double reviewAverage;
    private int trainerCount;
    private int reviewCount;
    private String reviewSummary;
    private List<String> imageNames;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedAt;
}
