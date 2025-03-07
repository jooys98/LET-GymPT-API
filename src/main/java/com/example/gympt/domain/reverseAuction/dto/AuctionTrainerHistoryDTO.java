package com.example.gympt.domain.reverseAuction.dto;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@ToString
public class AuctionTrainerHistoryDTO {
    private Long auctionId;
    private String name;
    private String title;
    private String request;
    private String medicalConditions;
    private LocalDateTime openAt;
    private Long finalPrice;
    private LocalDateTime closeAt;
    private String localName;
    private Long localId;
    private String gender;
    private String status;
}
