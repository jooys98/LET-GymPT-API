package com.example.gympt.domain.trainer.service;

import com.example.gympt.domain.booking.dto.BookingResponseDTO;
import com.example.gympt.domain.gym.entity.Gym;
import com.example.gympt.domain.member.dto.MemberResponseDTO;
import com.example.gympt.domain.member.entity.Member;
import com.example.gympt.domain.reverseAuction.dto.AuctionTrainerBidResponseDTO;
import com.example.gympt.domain.reverseAuction.dto.TrainerAuctionRequestDTO;
import com.example.gympt.domain.trainer.dto.TrainerRequestDTO;
import com.example.gympt.domain.trainer.dto.TrainerResponseDTO;
import com.example.gympt.domain.trainer.dto.TrainerSaveRequestDTO;
import com.example.gympt.domain.trainer.entity.TrainerImage;
import com.example.gympt.domain.trainer.entity.TrainerSaveForm;
import com.example.gympt.domain.trainer.entity.Trainers;
import com.example.gympt.dto.PageRequestDTO;
import com.example.gympt.dto.PageResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;


public interface TrainerService {

    void saveTrainer(String TrainerEmail, TrainerSaveRequestDTO trainerSaveRequestDTO);

    PageResponseDTO<TrainerResponseDTO> getTrainers(TrainerRequestDTO trainerRequestDTO, PageRequestDTO pageRequestDTO, String email);

    TrainerResponseDTO getTrainerById(Long id, String email);

    AuctionTrainerBidResponseDTO applyAuction(String TrainerEmail, TrainerAuctionRequestDTO trainerAuctionRequestDTO);

    AuctionTrainerBidResponseDTO changePrice(Long auctionRequestId, String trainerEmail, Long updatePrice);

    void changeByGym(Gym gym);

    List<TrainerResponseDTO> getTrainerByGymId(Long id, String email);

    Long updateTrainer(String email, TrainerSaveRequestDTO trainerSaveRequestDTO);

    TrainerResponseDTO getTrainerDetail(String email);


    List<TrainerResponseDTO> getTrainerListByLocal(String email, Long localId);
}
