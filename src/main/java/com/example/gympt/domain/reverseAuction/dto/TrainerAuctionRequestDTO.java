package com.example.gympt.domain.reverseAuction.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@ToString
public class TrainerAuctionRequestDTO {
    //트레이너의 입찰 신청
    private String trainerName;
    //트레이너 성함
    private String trainerEmail;
    //트레이너 이메일
    private String localName;
    private Long localId;

    //트레이너의 사는 지역
    private String gymName;
    //헬스장 이름
    private Long auctionRequestId;
    //경매 번호
    private Long price;
    //제안 가격
    private String proposalContent;
//소개 , 어필 글

}
