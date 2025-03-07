package com.example.gympt.domain.reverseAuction.service;

import com.example.gympt.domain.member.entity.Member;
import com.example.gympt.domain.reverseAuction.dto.*;
import com.example.gympt.domain.reverseAuction.entity.AuctionRequest;
import com.example.gympt.domain.reverseAuction.entity.AuctionTrainerBid;
import com.example.gympt.domain.reverseAuction.entity.MatchedAuction;
import com.example.gympt.domain.trainer.dto.TrainerResponseDTO;

import java.util.List;

public interface ReverseAuctionService {
    Long applyAuction(AuctionRequestDTO auctionRequestDTO);

    FinalSelectAuctionDTO selectTrainer(String email, Long trainerId);

    List<AuctionResponseDTO> getAuctionList();

    default AuctionResponseDTO AuctionEntityToDTO(AuctionRequest auctionRequest) {
        AuctionResponseDTO auctionResponseDTO = AuctionResponseDTO.builder()
                .auctionId(auctionRequest.getId())
                .title(auctionRequest.getTitle())
                .name(auctionRequest.getMember().getName())
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

    List<AuctionResponseToTrainerDTO> getAuctionListToTrainers();

    default AuctionResponseToTrainerDTO AuctionEntityForTrainersToDTO(AuctionRequest auctionRequest) {
        return AuctionResponseToTrainerDTO.builder()
                .auctionId(auctionRequest.getId())
                .title(auctionRequest.getTitle())
                .request(auctionRequest.getRequest())
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

    default FinalSelectAuctionDTO convertToSelectDTO(AuctionRequest auctionRequest, MatchedAuction matchedAuction) {
        return FinalSelectAuctionDTO.builder()
                .auctionId(auctionRequest.getId())
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
                .status(auctionRequest.getStatus().toString())
                .build();
    }

//
//    default AuctionTrainerNotificationDTO convertToNotificationDTO(MatchedAuction matchedAuction) {
//        return AuctionTrainerNotificationDTO.builder()
//                .type("AUCTION_SELECTED")
//                .memberEmail(matchedAuction.getAuctionRequest().getMember().getEmail())
//                .memberPhoneNumber(matchedAuction.getAuctionRequest().getMember().getPhone())
//                .memberName(matchedAuction.getAuctionRequest().getMember().getName())
//                .auctionId(matchedAuction.getAuctionRequest().getId())
//                .finalPrice(matchedAuction.getFinalPrice())
//                .message("축하드립니다! 회원님이 귀하를 PT 트레이너로 선택했습니다")
//                .build();
//    }


    default AuctionTrainerBidResponseDTO convertToAuctionTrainerBidDTO(AuctionTrainerBid auctionTrainerBid) {
        return AuctionTrainerBidResponseDTO.builder()
                .auctionRequestId(auctionTrainerBid.getAuctionRequest().getId())
                .trainerId(auctionTrainerBid.getTrainer().getId())
                .trainerEmail(auctionTrainerBid.getTrainer().getMember().getEmail())
                .trainerName(auctionTrainerBid.getTrainer().getMember().getName())
                .gymId(auctionTrainerBid.getTrainer().getGym().getId())
                .gymName(auctionTrainerBid.getTrainer().getGym().getGymName())
                .price(auctionTrainerBid.getPrice())
                .proposalContent(auctionTrainerBid.getProposalContent())
                .trainerImage(auctionTrainerBid.getTrainerImage())
                .startTime(auctionTrainerBid.getCreatedAt())
                .build();
    }

    default AuctionTrainerHistoryDTO convertToAuctionTrainerHistory(AuctionTrainerBid auctionTrainerBid) {
        return AuctionTrainerHistoryDTO.builder()
                .auctionId(auctionTrainerBid.getAuctionRequest().getId())
                .name(auctionTrainerBid.getAuctionRequest().getMember().getName())
                .title(auctionTrainerBid.getAuctionRequest().getTitle())
                .request(auctionTrainerBid.getAuctionRequest().getRequest())
                .openAt(auctionTrainerBid.getAuctionRequest().getCreatedAt())
                .closeAt(auctionTrainerBid.getClosedAt())
                .finalPrice(auctionTrainerBid.getPrice())
                .medicalConditions(auctionTrainerBid.getAuctionRequest().getMedicalConditions())
                .localName(auctionTrainerBid.getAuctionRequest().getLocal().getLocalName())
                .localId(auctionTrainerBid.getAuctionRequest().getLocal().getId())
                .gender(auctionTrainerBid.getAuctionRequest().getGender().toString())
                .status(auctionTrainerBid.getStatus().toString())
                .build();
    }


    Object getAuction(Long auctionRequestId, String email);

    //    AuctionResponseToTrainerDTO getAuctionToTrainer(Long auctionId);
//    AuctionTrainerNotificationDTO getSelectedMessage(String email);
    Member getMember(String email);

    List<AuctionResponseDTO> getAuctionListInLocal(Long localId);

    List<AuctionResponseToTrainerDTO> getAuctionListToTrainersInLocal(Long localId);

    Long cancelAuction(String email, Long auctionRequestId);

    List<AuctionTrainerBidResponseDTO> getTrainers(Long auctionRequestId);


    List<FinalSelectAuctionDTO> getAuctionHistory(String email);

    List<AuctionTrainerHistoryDTO> getAuctionHistoryToTrainer(String email);
}
