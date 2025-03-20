package com.example.gympt.domain.reverseAuction.dto;

import com.example.gympt.domain.gym.entity.GymImage;
import com.example.gympt.domain.reverseAuction.entity.AuctionRequest;
import com.example.gympt.domain.reverseAuction.entity.MatchedAuction;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@ToString
public class FinalSelectAuctionDTO {
    //유저에게 보내지는 트레이너 정보 , 최종 매칭된 내역 트레이너 보기
    private Long auctionId;
    //경매 번호
    private String email;
    //유저 이메일
    private Long finalPrice;
    //최종금액
    private String trainerName;

    private String trainerImage;
    //트레이너 이미지
    private LocalDateTime createAt;
    private LocalDateTime closedAt;
    //역경매 종료 시간
    private Long localId;
    private String localName;
    private String parentLocal;
    private String childLocal;
    //지역 이름
    private String gymAddress;
    //헬스장 주소
    private String gymName;
    private String gymImage;
    private String status;

    public static FinalSelectAuctionDTO from(AuctionRequest auctionRequest, MatchedAuction matchedAuction) {
        return FinalSelectAuctionDTO.builder()
                .auctionId(auctionRequest.getId())
                .parentLocal(auctionRequest.getLocal().getParent().toString())
                .childLocal(auctionRequest.getLocal().getChildren().toString())
                .email(auctionRequest.getMember().getEmail())
                .finalPrice(matchedAuction.getFinalPrice())
                .trainerName(matchedAuction.getAuctionTrainerBid().getTrainer().getTrainerName())
                .trainerImage(matchedAuction.getAuctionTrainerBid().getTrainerImage())
                .createAt(auctionRequest.getCreatedAt())
                .closedAt(matchedAuction.getClosedAt())
                .localId(auctionRequest.getLocal().getId())
                .localName(auctionRequest.getLocal().getLocalName())
                .gymAddress(matchedAuction.getAuctionTrainerBid().getTrainer().getGym().getAddress())
                .gymName(matchedAuction.getAuctionTrainerBid().getTrainer().getGym().getGymName())
                .gymImage(matchedAuction.getAuctionTrainerBid().getTrainer().getGym().getImageList().stream().map(GymImage::getGymImageName).findFirst().toString())
                .status(auctionRequest.getStatus().toString())
                .build();
    }

}

