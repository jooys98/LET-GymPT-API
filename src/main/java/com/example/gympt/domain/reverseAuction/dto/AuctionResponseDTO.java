package com.example.gympt.domain.reverseAuction.dto;

import com.example.gympt.domain.reverseAuction.entity.AuctionRequest;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@ToString
public class AuctionResponseDTO {
    //유저 , 트레이너 둘다 보여질 역경매 게시판
    //민감한 개인 정보는 트레이너에게만 보여지게 dto클래스를 따로 만들어서 처리
    private Long auctionId;
    private String memberProfileImage;
    private String parentLocal;
    private String childLocal;
    private String title;
    private String name;
    private String request;
    private String medicalConditions;
    private LocalDateTime openAt;
    private String localName;
    private Long localId;
    private Long age;
    private String gender;
    private Integer participateTrainers;
    private String status;

    public static AuctionResponseDTO from(AuctionRequest auctionRequest) {
        AuctionResponseDTO auctionResponseDTO = AuctionResponseDTO.builder()
                .auctionId(auctionRequest.getId())
                .title(auctionRequest.getTitle())
                .name(auctionRequest.getMember().getName())
                .memberProfileImage(auctionRequest.getMember().getProfileImage())
                .childLocal(auctionRequest.getLocal().getChildren().toString())
                .parentLocal(auctionRequest.getLocal().getParent().toString())
                .request(auctionRequest.getRequest())
                .medicalConditions(auctionRequest.getMedicalConditions())
                .openAt(auctionRequest.getCreatedAt())
                .localName(auctionRequest.getLocal().getLocalName())
                .localId(auctionRequest.getLocal().getId())
                .age(auctionRequest.getAge())
                .gender(auctionRequest.getGender().toString())
                .participateTrainers(auctionRequest.getParticipateTrainers())
                .status(auctionRequest.getStatus().toString())
                .build();
        return auctionResponseDTO;
    }
}