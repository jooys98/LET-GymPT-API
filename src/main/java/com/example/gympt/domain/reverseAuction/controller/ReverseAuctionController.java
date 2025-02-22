package com.example.gympt.domain.reverseAuction.controller;

import com.example.gympt.domain.reverseAuction.dto.AuctionRequestDTO;
import com.example.gympt.domain.reverseAuction.dto.AuctionResponseDTO;
import com.example.gympt.domain.reverseAuction.dto.AuctionTrainerNotificationDTO;
import com.example.gympt.domain.reverseAuction.dto.FinalSelectAuctionDTO;
import com.example.gympt.domain.reverseAuction.entity.MatchedAuction;
import com.example.gympt.domain.reverseAuction.service.ReverseAuctionService;
import com.example.gympt.security.MemberAuthDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reverse-auction")
public class ReverseAuctionController {
//TODO: 유저 , 트레이너 email 파라미터로 받는 api 전부 @AuthenticationPrincipal 로 바꿔주기
//로그인 후 이용 가능한 서비스

    private final ReverseAuctionService reverseAuctionService;
    private final SimpMessageSendingOperations messagingTemplate;

    //역경매 신청
    @PostMapping("/apply")
    public ResponseEntity<String> applyReverseAuction(@AuthenticationPrincipal final MemberAuthDTO memberAuthDTO, @RequestBody AuctionRequestDTO auctionRequestDTO) {
        auctionRequestDTO.setEmail(memberAuthDTO.getUsername());
        reverseAuctionService.applyAuction(auctionRequestDTO);
        return ResponseEntity.ok("역경매 신청이 완료 되었습니다");
    }


    //트레이너 낙찰
    @PostMapping("/select")
    public ResponseEntity<FinalSelectAuctionDTO> selectFinalTrainer(@AuthenticationPrincipal final MemberAuthDTO memberAuthDTO, @RequestParam String trainerEmail) {
        FinalSelectAuctionDTO finalSelectAuctionDTO = reverseAuctionService.selectTrainer(memberAuthDTO.getUsername(), trainerEmail);
        return ResponseEntity.ok(finalSelectAuctionDTO);
    }

    //로그인 한 회원/트레이너 는 전부 볼 수 있는 역경매 게시판 !
    @GetMapping("/list")
    public ResponseEntity<List<AuctionResponseDTO>> getAuctionList() {
        List<AuctionResponseDTO> auctionResponseDTOS = reverseAuctionService.getAuctionList();
        return ResponseEntity.ok(auctionResponseDTOS);

    }

    //역경매 글 상세보기 (민감한 정보는 트레이너 에게만 !)
    @GetMapping("/list/{auctionRequestId}")
    public ResponseEntity<AuctionResponseDTO> getAuctionDetailById(@PathVariable Long auctionRequestId) {
        AuctionResponseDTO auctionResponseDTO = reverseAuctionService.getAuction(auctionRequestId);
        return ResponseEntity.ok(auctionResponseDTO);
    }


    @MessageMapping("/notifications")
    public void handleMessage(String trainerEmail, StompHeaderAccessor stompHeaderAccessor) {
        Authentication authentication = (Authentication) stompHeaderAccessor.getUser();
        MemberAuthDTO member = (MemberAuthDTO) authentication.getPrincipal();
        // roomId가 null 인 경우 (첫 메시지) 채팅방 생성 후 메시지 저장
        // 생성된 채팅방 ID 설정
        AuctionTrainerNotificationDTO notificationDTO = reverseAuctionService.getSelectedMessage(member.getUsername());

        // 웹소켓으로 트레이너에게 알림 전송
        messagingTemplate.convertAndSendToUser(
                trainerEmail,  // 트레이너의 이메일 (구독 식별자)
                "/queue/notifications",  // 클라이언트에서 받는 구독 주소
                notificationDTO);
    }
}
