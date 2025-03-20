package com.example.gympt.domain.reverseAuction.dto;

import com.example.gympt.domain.reverseAuction.entity.AuctionTrainerBid;
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


    public static AuctionTrainerBidResponseDTO from(AuctionTrainerBid auctionTrainerBid) {
        return AuctionTrainerBidResponseDTO.builder()
                .auctionRequestId(auctionTrainerBid.getAuctionRequest().getId())
                .trainerId(auctionTrainerBid.getTrainer().getId())
                .trainerEmail(auctionTrainerBid.getTrainer().getMember().getEmail())
                .trainerName(auctionTrainerBid.getTrainer().getMember().getName())
                .gymId(auctionTrainerBid.getTrainer().getGym().getId())
                .gymName(auctionTrainerBid.getTrainer().getGym().getGymName())
                .price(auctionTrainerBid.getPrice())
                .proposalContent(auctionTrainerBid.getProposalContent())
                .trainerImage(auctionTrainerBid.getTrainerImage())
                .startTime(auctionTrainerBid.getCreatedAt())
                .build();
    }
}
