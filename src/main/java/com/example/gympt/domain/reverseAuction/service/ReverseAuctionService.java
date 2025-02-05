package com.example.gympt.domain.reverseAuction.service;

import com.example.gympt.domain.reverseAuction.dto.AuctionRequestDTO;
import com.example.gympt.domain.reverseAuction.dto.AuctionResponseDTO;
import com.example.gympt.domain.reverseAuction.dto.AuctionResponseToTrainerDTO;
import com.example.gympt.domain.reverseAuction.dto.FinalSelectAuctionDTO;
import com.example.gympt.domain.reverseAuction.entity.AuctionRequest;

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
                .gender(auctionRequest.getGender())
                .participateTrainers(auctionRequest.getParticipateTrainers())
                .build();
        return auctionResponseDTO;

    }

    List<AuctionResponseToTrainerDTO> getAuctionListToTrainers();

    default AuctionResponseToTrainerDTO AuctionEntityForTrainersToDTO(AuctionRequest auctionRequest) {
        AuctionResponseToTrainerDTO auctionResponseToTrainerDTO = AuctionResponseToTrainerDTO.builder()
                .auctionId(auctionRequest.getId())
                .title(auctionRequest.getTitle())
                .request(auctionRequest.getRequest())
                .medicalConditions(auctionRequest.getMedicalConditions())
                .openAt(auctionRequest.getCreatedAt())
                .localName(auctionRequest.getLocal().getLocalName())
                .age(auctionRequest.getAge())
                .gender(auctionRequest.getGender())
                .email(auctionRequest.getMember().getEmail())
                .name(auctionRequest.getMember().getName())
                .weight(auctionRequest.getWeight())
                .height(auctionRequest.getHeight())
                .participateTrainers(auctionRequest.getParticipateTrainers())
                .build();
        return auctionResponseToTrainerDTO;
    }

    AuctionResponseDTO getAuction(Long auctionRequestId);

    AuctionResponseToTrainerDTO getAuctionToTrainer(Long auctionId);
}
