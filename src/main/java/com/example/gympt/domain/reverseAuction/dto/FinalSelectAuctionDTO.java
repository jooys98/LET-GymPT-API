package com.example.gympt.domain.reverseAuction.dto;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@ToString
public class FinalSelectAuctionDTO {

    //유저에게 보내지는 트레이너 정보
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
    //지역 이름
    private String gymAddress;
    //헬스장 주소
    private String gymName;

    private String status;

}

