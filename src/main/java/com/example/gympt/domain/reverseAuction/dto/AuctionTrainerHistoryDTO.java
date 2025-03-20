package com.example.gympt.domain.reverseAuction.dto;

import com.example.gympt.domain.reverseAuction.entity.AuctionTrainerBid;
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

    public static AuctionTrainerHistoryDTO from(AuctionTrainerBid auctionTrainerBid) {
        return AuctionTrainerHistoryDTO.builder()
                .auctionId(auctionTrainerBid.getAuctionRequest().getId())
                .name(auctionTrainerBid.getAuctionRequest().getMember().getName())
                .title(auctionTrainerBid.getAuctionRequest().getTitle())
                .request(auctionTrainerBid.getAuctionRequest().getRequest())
                .openAt(auctionTrainerBid.getAuctionRequest().getCreatedAt())
                .closeAt(auctionTrainerBid.getClosedAt())
                .finalPrice(auctionTrainerBid.getPrice())
                .medicalConditions(auctionTrainerBid.getAuctionRequest().getMedicalConditions())
                .localName(auctionTrainerBid.getAuctionRequest().getLocal().getLocalName())
                .localId(auctionTrainerBid.getAuctionRequest().getLocal().getId())
                .gender(auctionTrainerBid.getAuctionRequest().getGender().toString())
                .status(auctionTrainerBid.getStatus().toString())
                .build();
    }
}
