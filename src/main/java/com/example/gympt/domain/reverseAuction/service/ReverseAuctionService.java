package com.example.gympt.domain.reverseAuction.service;

import com.example.gympt.domain.gym.entity.GymImage;
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

    List<AuctionResponseToTrainerDTO> getAuctionListToTrainers();

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
