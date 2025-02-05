package com.example.gympt.domain.reverseAuction.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
@Getter
@Builder
//트레이너에게 전달될 메세지
public class AuctionTrainerNotificationDTO {
    private String type;
    private String memberEmail;
    private String memberPhoneNumber;
    private String memberName;
    private Long auctionId;
    private Long finalPrice;
    private String message;
    private LocalDateTime timestamp;
}
