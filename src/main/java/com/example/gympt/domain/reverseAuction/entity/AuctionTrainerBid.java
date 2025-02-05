package com.example.gympt.domain.reverseAuction.entity;

import com.example.gympt.domain.trainer.entity.TrainerImage;
import com.example.gympt.domain.trainer.entity.Trainers;
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
@Table(name = "auction_trainer_bid")
public class AuctionTrainerBid extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "auction_request_id")
    private AuctionRequest auctionRequest;

    @ManyToOne
    @JoinColumn(name = "trainer_email")
    private Trainers trainer;

    @OneToOne(mappedBy = "auctionTrainerBid")
    private MatchedAuction matchedAuction;

    private Long price;
    private String proposalContent;
    private String trainerImage;

}
