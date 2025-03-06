package com.example.gympt.domain.reverseAuction.dto;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class AuctionResponseToTrainerDTO {
    private String email;
    private String name;
    private Long height;
    //키
    private Long weight;
    //몸무게
    private Long auctionId;
    private String title;
    private String request;
    private String medicalConditions;
    private LocalDateTime openAt;
    private String localName;
    private Long localId;
    private Long age;
    private String gender;
    private Integer participateTrainers;

    //TODO: 참여한 트레이너 들 + 가격+ 날짜 리스트도 함께 포함 시키기
}
