package com.example.gympt.domain.reverseAuction.controller;

import com.example.gympt.domain.reverseAuction.dto.*;
import com.example.gympt.domain.reverseAuction.service.ReverseAuctionService;
import com.example.gympt.domain.trainer.service.TrainerService;
import com.example.gympt.security.MemberAuthDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class AuctionWebSocketController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ReverseAuctionService reverseAuctionService;
    private final TrainerService trainerService;

    @MessageMapping("/bid")

    public void processBid(TrainerAuctionRequestDTO bidDTO, StompHeaderAccessor stompHeaderAccessor) {
        // 입찰 처리 로직
        Authentication authentication = (Authentication) stompHeaderAccessor.getUser();
        MemberAuthDTO member = (MemberAuthDTO) authentication.getPrincipal();
        AuctionTrainerBidResponseDTO response = trainerService.applyAuction(member.getEmail(), bidDTO);

        // 해당 경매 topic으로 최신 입찰 정보 전송
        messagingTemplate.convertAndSend("/topic/auction/" + bidDTO.getAuctionRequestId(), response);
    }

    // 역경매 가격 변경
    @MessageMapping("/status/price")
    public void updateAuctionStatus(Long auctionRequestId, AuctionUpdatePrice auctionUpdatePrice, StompHeaderAccessor stompHeaderAccessor) {
        Authentication authentication = (Authentication) stompHeaderAccessor.getUser();
        MemberAuthDTO member = (MemberAuthDTO) authentication.getPrincipal();
        AuctionTrainerBidResponseDTO response = trainerService.changePrice(auctionRequestId, member.getEmail(), auctionUpdatePrice.getUpdatePrice());

        // 해당 경매 topic으로 상태 변경 정보 전송
        messagingTemplate.convertAndSend("/topic/auction/" + response.getAuctionRequestId(), response);
    }

    // 역경매 최종 선택
    @MessageMapping("/status/select")
    public void selectAuctionStatus(SelectedTrainerDTO selectedTrainerDTO, StompHeaderAccessor stompHeaderAccessor) {
        // 상태 업데이트 로직
        Authentication authentication = (Authentication) stompHeaderAccessor.getUser();
        MemberAuthDTO member = (MemberAuthDTO) authentication.getPrincipal();
        FinalSelectAuctionDTO response = reverseAuctionService.selectTrainer(member.getEmail(), selectedTrainerDTO.getTrainerId());

        // 해당 경매 topic으로 최종 선택 상태 변경 정보 전송
        messagingTemplate.convertAndSend("/topic/auction/" + response.getAuctionId(), response);
    }


}
