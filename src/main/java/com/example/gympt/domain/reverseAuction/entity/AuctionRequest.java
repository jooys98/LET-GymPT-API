package com.example.gympt.domain.reverseAuction.entity;

import com.example.gympt.domain.category.entity.Local;
import com.example.gympt.domain.member.entity.Member;
import com.example.gympt.domain.reverseAuction.enums.AuctionStatus;
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
@Table(name = "auction_request")
public class AuctionRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToOne
    @JoinColumn(name = "email")
    private Member member;
    private Long age;
    //member 정보 가져오기
    private String gender;
    //성별
    private Long height;
    //키
    private Long weight;
    //몸무게
    private String title;
    //역경매 요청시 제목으로 이용자 어필
    private String request;
    //pt 요구사항 (원하는 스타일)
    private String medicalConditions;
    //신체 결함 사항

//참가한 트레이너 수
    public Integer getParticipateTrainers() {
        return this.auctionTrainerBids.size();
    }

    @OneToOne(mappedBy = "auctionRequest")
    private MatchedAuction matchedAuction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "local_id")
    private Local local;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "status_list", joinColumns = @JoinColumn(name = "title")) //상태와 연결되는 컬럼값
    @Column(name = "status") // 해당 List 를 저장할 컬럼명
    @Builder.Default
    private List<AuctionStatus> status = new ArrayList<>();

    @OneToMany(mappedBy = "auctionRequest")
    private List<AuctionTrainerBid> auctionTrainerBids = new ArrayList<>();

}
