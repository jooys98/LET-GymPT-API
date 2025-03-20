package com.example.gympt.domain.reverseAuction.controller;

import com.example.gympt.domain.member.entity.Member;
import com.example.gympt.domain.member.enums.MemberRole;
import com.example.gympt.domain.reverseAuction.dto.*;
import com.example.gympt.domain.reverseAuction.service.ReverseAuctionService;
import com.example.gympt.domain.trainer.dto.TrainerResponseDTO;
import com.example.gympt.security.MemberAuthDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reverse-auction")
public class ReverseAuctionController {
    //로그인 후 이용 가능한 서비스
    private final SimpMessageSendingOperations messagingTemplate;
    private final ReverseAuctionService reverseAuctionService;

    //역경매 신청
    @PostMapping("/apply")
    public ResponseEntity<Long> applyReverseAuction(@RequestBody AuctionRequestDTO auctionRequestDTO) {
        return ResponseEntity.ok(reverseAuctionService.applyAuction(auctionRequestDTO));
    }


    //트레이너 낙찰
    @PostMapping("/select")
    public ResponseEntity<FinalSelectAuctionDTO> selectFinalTrainer(@AuthenticationPrincipal final MemberAuthDTO memberAuthDTO, @RequestBody SelectedTrainerDTO selectedTrainerDTO) {
        FinalSelectAuctionDTO finalSelectAuctionDTO = reverseAuctionService.selectTrainer(memberAuthDTO.getEmail(), selectedTrainerDTO.getTrainerId());
        return ResponseEntity.ok(finalSelectAuctionDTO);
    }

    //역경매에 참여한 트레이너 리스트 보기
    @GetMapping("/trainers/{auctionRequestId}")
    public ResponseEntity<List<AuctionTrainerBidResponseDTO>> getTrainersInAuction(@PathVariable Long auctionRequestId) {
        return ResponseEntity.ok(reverseAuctionService.getTrainers(auctionRequestId));
    }


    //로그인 한 회원/트레이너 는 전부 볼 수 있는 역경매 게시판 !
    @GetMapping("/list")
    public ResponseEntity<List<?>> getAuctionList(@AuthenticationPrincipal final MemberAuthDTO memberAuthDTO) {

        Member member = reverseAuctionService.getMember(memberAuthDTO.getEmail());

        if (member.getMemberRoleList().contains(MemberRole.USER)) {
            List<AuctionResponseDTO> userList =
                    reverseAuctionService.getAuctionList();
            return ResponseEntity.ok(userList);
        } else {
            List<AuctionResponseToTrainerDTO> trainerList =
                    reverseAuctionService.getAuctionListToTrainers();
            return ResponseEntity.ok(trainerList);
        }

    }

    //역경매 글 상세보기 (민감한 정보는 트레이너 에게만 !)
    @GetMapping("/{auctionRequestId}")
    public ResponseEntity<?> getAuctionDetailById(@AuthenticationPrincipal final MemberAuthDTO memberAuthDTO, @PathVariable Long auctionRequestId) {
        Object auctionResponseDTO = reverseAuctionService.getAuction(auctionRequestId, memberAuthDTO.getEmail());
        return ResponseEntity.ok(auctionResponseDTO);
    }

    //지역별 역경매 조회
    @GetMapping("/local/{localId}")
    public ResponseEntity<List<?>> getLocalAuction(@AuthenticationPrincipal final MemberAuthDTO memberAuthDTO, @PathVariable Long localId) {
        Member member = reverseAuctionService.getMember(memberAuthDTO.getEmail());

        if (member.getMemberRoleList().contains(MemberRole.USER)) {
            List<AuctionResponseDTO> userList =
                    reverseAuctionService.getAuctionListInLocal(localId);
            return ResponseEntity.ok(userList);
        } else {
            List<AuctionResponseToTrainerDTO> trainerList =
                    reverseAuctionService.getAuctionListToTrainersInLocal(localId);
            return ResponseEntity.ok(trainerList);
        }

    }

    //사용자의 역경매 취소 로직
    @DeleteMapping("/{auctionRequestId}")
    public ResponseEntity<Long> deleteAuction(@AuthenticationPrincipal final MemberAuthDTO memberAuthDTO, @PathVariable Long auctionRequestId) {
        return ResponseEntity.ok(reverseAuctionService.cancelAuction(memberAuthDTO.getEmail(), auctionRequestId));
    }

    //회원의 매칭된 역경매 내역 조회
    @GetMapping("/history")
    public ResponseEntity<List<FinalSelectAuctionDTO>> getHistory(@AuthenticationPrincipal final MemberAuthDTO memberAuthDTO) {
        return ResponseEntity.ok(reverseAuctionService.getAuctionHistory(memberAuthDTO.getEmail()));
    }

}
