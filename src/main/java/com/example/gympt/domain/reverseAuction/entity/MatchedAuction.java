package com.example.gympt.domain.reverseAuction.entity;

import com.example.gympt.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@SuperBuilder
@Setter
@AllArgsConstructor
@Getter
@NoArgsConstructor
@Entity
@Table(name = "matched_action")
@ToString
public class MatchedAuction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "auction_request_id")
    private AuctionRequest auctionRequest;

    @OneToOne
    @JoinColumn(name = "auction_trainer_bid_id")
    private AuctionTrainerBid auctionTrainerBid;

    private Long finalPrice; // 최종 pt 가격


    public static MatchedAuction from(AuctionRequest auctionRequest, AuctionTrainerBid auctionTrainerBid) {
        return MatchedAuction.builder()
                .auctionTrainerBid(auctionTrainerBid)
                .auctionRequest(auctionRequest)
                .finalPrice(auctionTrainerBid.getPrice()) // 트레이너가 제안한 최종 금액
                .build();
    }

}
