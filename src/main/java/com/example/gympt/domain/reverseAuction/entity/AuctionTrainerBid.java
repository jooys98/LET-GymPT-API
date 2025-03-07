package com.example.gympt.domain.reverseAuction.entity;

import com.example.gympt.domain.reverseAuction.enums.AuctionStatus;
import com.example.gympt.domain.reverseAuction.enums.AuctionTrainerStatus;
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
    @JoinColumn(name = "trainer_id")
    private Trainers trainer;

    @OneToOne(mappedBy = "auctionTrainerBid", cascade = CascadeType.ALL)
    private MatchedAuction matchedAuction;

    private Long price;
    private String proposalContent;
    private String trainerImage;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "status_list_trainers", joinColumns = @JoinColumn(name = "title")) //상태와 연결되는 컬럼값
    @Column(name = "status") // 해당 List 를 저장할 컬럼명
    @Builder.Default
    private List<AuctionTrainerStatus> status = new ArrayList<>();

    public void deleteStatus(AuctionTrainerStatus status) {
        this.status.remove(status);
    }


    public void changeStatus(AuctionTrainerStatus status) {
        // 이미 해당 상태가 존재하는지 확인
        if (!this.status.contains(status)) {
            this.status.add(status);
        }
    }


    public void changePrice(Long price) {
        this.price = price;
    }
}
