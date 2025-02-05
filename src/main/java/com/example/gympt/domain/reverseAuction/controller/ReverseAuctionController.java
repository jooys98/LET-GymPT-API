package com.example.gympt.domain.reverseAuction.controller;

import com.example.gympt.domain.reverseAuction.dto.AuctionRequestDTO;
import com.example.gympt.domain.reverseAuction.dto.AuctionResponseDTO;
import com.example.gympt.domain.reverseAuction.dto.FinalSelectAuctionDTO;
import com.example.gympt.domain.reverseAuction.service.ReverseAuctionService;
import com.example.gympt.security.MemberAuthDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reverse-auction")
public class ReverseAuctionController {
//TODO: 유저 , 트레이너 email 파라미터로 받는 api 전부 @AuthenticationPrincipal 로 바꿔주기

    private final ReverseAuctionService reverseAuctionService;

    @PostMapping("/apply")
    public ResponseEntity<String> applyReverseAuction(@AuthenticationPrincipal MemberAuthDTO memberAuthDTO, @RequestBody AuctionRequestDTO auctionRequestDTO) {
        auctionRequestDTO.setEmail(memberAuthDTO.getUsername());
        reverseAuctionService.applyAuction(auctionRequestDTO);
        return ResponseEntity.ok("역경매 신청이 완료 되었습니다");
    }

    @PostMapping("/select")
    public ResponseEntity<FinalSelectAuctionDTO> selectFinalTrainer( @AuthenticationPrincipal MemberAuthDTO memberAuthDTO,@RequestParam String trainerEmail) {
        FinalSelectAuctionDTO finalSelectAuctionDTO = reverseAuctionService.selectTrainer(memberAuthDTO.getUsername(), trainerEmail);
        return ResponseEntity.ok(finalSelectAuctionDTO);
    }
    //로그인 한 회원/트레이너 는 전부 볼 수 있는 역경매 게시판 !
    @GetMapping("/list")
    public ResponseEntity<List<AuctionResponseDTO>> getAuctionList() {
        List<AuctionResponseDTO> auctionResponseDTOS = reverseAuctionService.getAuctionList();
        return ResponseEntity.ok(auctionResponseDTOS);
    }

}
