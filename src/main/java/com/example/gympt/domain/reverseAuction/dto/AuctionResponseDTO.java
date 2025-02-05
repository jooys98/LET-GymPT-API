package com.example.gympt.domain.reverseAuction.dto;

import lombok.*;

import java.time.LocalDateTime;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class AuctionResponseDTO {
    //유저 , 트레이너 둘다 보여질 역경매 게시판
    //민감한 개인 정보는 트레이너에게만 보여지게 dto클래스를 따로 만들어서 처리
    private Long auctionId;
    private String title;
    private String request;
    private String medicalConditions;
    private LocalDateTime openAt;
    private String localName;
    private Long age;
    private String gender;
}
