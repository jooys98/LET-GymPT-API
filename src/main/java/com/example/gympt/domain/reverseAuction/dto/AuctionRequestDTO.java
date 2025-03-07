package com.example.gympt.domain.reverseAuction.dto;

import com.example.gympt.domain.member.entity.Member;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@ToString
//사용자의 역경매 신청 양식
public class AuctionRequestDTO {
    private String email;
    private String name;
    private Long localId;
    private String localName;
    private Long age;
    //member 정보 가져오기
    private String gender;
    //성별 M,F
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
}
