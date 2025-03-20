package com.example.gympt.domain.reverseAuction.dto;

import com.example.gympt.domain.reverseAuction.entity.AuctionRequest;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@ToString
public class AuctionResponseToTrainerDTO {
    private String email;
    private String memberProfileImage;
    private String name;
    private Long height;
    //키
    private Long weight;
    //몸무게
    private Long auctionId;
    private String title;
    private String request;
    private String medicalConditions;
    private LocalDateTime openAt;
    private String parentLocal;
    private String childLocal;
    private String localName;
    private Long localId;
    private Long age;
    private String gender;
    private Integer participateTrainers; // 참여한 트레이너
    private String status;// 상태




    public static AuctionResponseToTrainerDTO from(AuctionRequest auctionRequest) {
        return AuctionResponseToTrainerDTO.builder()
                .auctionId(auctionRequest.getId())
                .title(auctionRequest.getTitle())
                .request(auctionRequest.getRequest())
                .memberProfileImage(auctionRequest.getMember().getProfileImage())
                .childLocal(auctionRequest.getLocal().getChildren().toString())
                .parentLocal(auctionRequest.getLocal().getParent().toString())
                .medicalConditions(auctionRequest.getMedicalConditions())
                .openAt(auctionRequest.getCreatedAt())
                .localId(auctionRequest.getLocal().getId())
                .localName(auctionRequest.getLocal().getLocalName())
                .age(auctionRequest.getAge())
                .gender(auctionRequest.getGender().toString())
                .email(auctionRequest.getMember().getEmail())
                .name(auctionRequest.getMember().getName())
                .weight(auctionRequest.getWeight())
                .height(auctionRequest.getHeight())
                .participateTrainers(auctionRequest.getParticipateTrainers())
                .status(auctionRequest.getStatus().toString())
                .build();

    }
}
