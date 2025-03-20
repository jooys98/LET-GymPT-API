package com.example.gympt.domain.reverseAuction.dto;

import com.example.gympt.domain.reverseAuction.entity.MatchedAuction;
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


    public static AuctionTrainerNotificationDTO from(MatchedAuction matchedAuction) {
        return AuctionTrainerNotificationDTO.builder()
                .type("AUCTION_SELECTED")
                .memberEmail(matchedAuction.getAuctionRequest().getMember().getEmail())
                .memberPhoneNumber(matchedAuction.getAuctionRequest().getMember().getPhone())
                .memberName(matchedAuction.getAuctionRequest().getMember().getName())
                .auctionId(matchedAuction.getAuctionRequest().getId())
                .finalPrice(matchedAuction.getFinalPrice())
                .message("축하드립니다! 회원님이 귀하를 PT 트레이너로 선택했습니다")
                .build();
    }
}
