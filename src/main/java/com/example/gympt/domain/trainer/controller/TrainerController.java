package com.example.gympt.domain.trainer.controller;

import com.example.gympt.domain.reverseAuction.dto.AuctionResponseToTrainerDTO;
import com.example.gympt.domain.reverseAuction.dto.TrainerAuctionRequestDTO;
import com.example.gympt.domain.reverseAuction.service.ReverseAuctionService;
import com.example.gympt.domain.trainer.dto.TrainerSaveRequestDTO;
import com.example.gympt.domain.trainer.service.TrainerService;
import com.example.gympt.dto.PageResponseDTO;
import com.example.gympt.security.MemberAuthDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainer") // 회원 권한 필요
@RequiredArgsConstructor
@Slf4j
public class TrainerController {

    private final TrainerService trainerService;
    private final ReverseAuctionService reverseAuctionService;

    //프래잰테이션 계층 (도메인 표현)
//트레이너 권한 회원만 이용할 수 있는 api
    @PostMapping("/apply")
    //트레이너 신청!!!
    public ResponseEntity<String> trainerApply(@AuthenticationPrincipal MemberAuthDTO memberAuthDTO,@RequestBody TrainerSaveRequestDTO trainerSaveRequestDTO) {
        trainerService.saveTrainer(memberAuthDTO.getUsername(),trainerSaveRequestDTO);
        return ResponseEntity.ok().body("트레이너 신청이 완료 되었습니댜!");
    }

    //역경매 신청
    @PostMapping("/auction")
    public ResponseEntity<String> letAuctionTrainer(@AuthenticationPrincipal MemberAuthDTO memberAuthDTO,@RequestBody TrainerAuctionRequestDTO trainerAuctionRequestDTO) {
        trainerService.applyAuction(memberAuthDTO.getUsername(),trainerAuctionRequestDTO);
        return ResponseEntity.ok().body("역경매 참여 완료 되었습니다");
    }

    //pt 가격 변경
    @PutMapping("/auction/update")
    public ResponseEntity<String> updateAuctionPrice(@AuthenticationPrincipal MemberAuthDTO memberAuthDTO,@RequestBody Long auctionRequestId, @RequestBody Long updatePrice) {
        trainerService.changePrice(auctionRequestId, memberAuthDTO.getUsername(), updatePrice);
        return ResponseEntity.ok("가격 변경이 완료 되었습니다 ");
    }

    //트레이너에게만 보여지는 역경매 정보
    @GetMapping("/auction/list")
    public ResponseEntity<List<AuctionResponseToTrainerDTO>> getAuctionRequestList() {
        List<AuctionResponseToTrainerDTO> response = reverseAuctionService.getAuctionListToTrainers();
        return ResponseEntity.ok().body(response);

    }
}
