package com.example.gympt.domain.reverseAuction.dto;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@ToString
public class AuctionTrainerBidResponseDTO {
    private Long auctionRequestId;
    private Long trainerId;
    private String trainerEmail;
    private String trainerName;
    private Long gymId;
    private String gymName;
    private Long price;
    private String proposalContent;
    private String trainerImage;
    private LocalDateTime startTime;

}
