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
    private Long age;
    private String gender;
    private Integer participateTrainers;
}
