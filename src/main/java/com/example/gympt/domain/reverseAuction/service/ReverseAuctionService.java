package com.example.gympt.domain.reverseAuction.service;

import com.example.gympt.domain.member.entity.Member;
import com.example.gympt.domain.reverseAuction.dto.*;
import com.example.gympt.domain.reverseAuction.entity.AuctionRequest;
import com.example.gympt.domain.reverseAuction.entity.MatchedAuction;

import java.util.List;

public interface ReverseAuctionService {
    void applyAuction(AuctionRequestDTO auctionRequestDTO);

    FinalSelectAuctionDTO selectTrainer(String email, String trainerEmail);

    List<AuctionResponseDTO> getAuctionList();

    default AuctionResponseDTO AuctionEntityToDTO(AuctionRequest auctionRequest) {
        AuctionResponseDTO auctionResponseDTO = AuctionResponseDTO.builder()
                .auctionId(auctionRequest.getId())
                .title(auctionRequest.getTitle())
                .request(auctionRequest.getRequest())
                .medicalConditions(auctionRequest.getMedicalConditions())
                .openAt(auctionRequest.getCreatedAt())
                .localName(auctionRequest.getLocal().getLocalName())
                .age(auctionRequest.getAge())
                .gender(auctionRequest.getGender().toString())
                .participateTrainers(auctionRequest.getParticipateTrainers())
                .build();
        return auctionResponseDTO;

    }

    List<AuctionResponseToTrainerDTO> getAuctionListToTrainers();

    default AuctionResponseToTrainerDTO AuctionEntityForTrainersToDTO(AuctionRequest auctionRequest) {
        return AuctionResponseToTrainerDTO.builder()
                .auctionId(auctionRequest.getId())
                .title(auctionRequest.getTitle())
                .request(auctionRequest.getRequest())
                .medicalConditions(auctionRequest.getMedicalConditions())
                .openAt(auctionRequest.getCreatedAt())
                .localName(auctionRequest.getLocal().getLocalName())
                .age(auctionRequest.getAge())
                .gender(auctionRequest.getGender().toString())
                .email(auctionRequest.getMember().getEmail())
                .name(auctionRequest.getMember().getName())
                .weight(auctionRequest.getWeight())
                .height(auctionRequest.getHeight())
                .participateTrainers(auctionRequest.getParticipateTrainers())
                .build();

    }

    default FinalSelectAuctionDTO convertToSelectDTO(AuctionRequest auctionRequest, MatchedAuction matchedAuction) {
        return FinalSelectAuctionDTO.builder()
                .auctionId(auctionRequest.getId())
                .email(matchedAuction.getAuctionRequest().getMember().getEmail())
                .finalPrice(matchedAuction.getFinalPrice())
                .trainerName(matchedAuction.getAuctionTrainerBid().getTrainer().getTrainerName())
                .closedAt(matchedAuction.getClosedAt())
                .trainerImage(matchedAuction.getAuctionTrainerBid().getTrainerImage())
                .localName(String.valueOf(matchedAuction.getAuctionTrainerBid().getTrainer().getLocal()))
                .gymAddress(matchedAuction.getAuctionTrainerBid().getTrainer().getGym().getAddress())
                .build();
    }


    default AuctionTrainerNotificationDTO convertToNotificationDTO(MatchedAuction matchedAuction) {
        return AuctionTrainerNotificationDTO.builder()
                .type("AUCTION_SELECTED")
                .memberEmail(matchedAuction.getAuctionRequest().getMember().getEmail())
                .memberPhoneNumber(matchedAuction.getAuctionRequest().getMember().getPhone())
                .memberName(matchedAuction.getAuctionRequest().getMember().getName())
                .auctionId(matchedAuction.getAuctionRequest().getId())
                .finalPrice(matchedAuction.getFinalPrice())
                .message("축하드립니다! 회원님이 귀하를 PT 트레이너로 선택했습니다")
                .build();
    }


    AuctionTrainerNotificationDTO getSelectedMessage(String email);


    Object getAuction(Long auctionRequestId, String email);

//    AuctionResponseToTrainerDTO getAuctionToTrainer(Long auctionId);

    Member getMember(String email);

    List<AuctionResponseDTO> getAuctionListInLocal(Long localId);

    List<AuctionResponseToTrainerDTO> getAuctionListToTrainersInLocal(Long localId);

    Long cancelAuction(String email, Long auctionRequestId);
}
