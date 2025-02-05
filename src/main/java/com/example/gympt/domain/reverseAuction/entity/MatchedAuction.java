package com.example.gympt.domain.reverseAuction.entity;

import com.example.gympt.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

    private Long finalPrice;
   // 최종 pt 가격
}
