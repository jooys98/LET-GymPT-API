package com.example.gympt.domain.trainer.controller;

import com.example.gympt.domain.reverseAuction.dto.TrainerAuctionRequestDTO;
import com.example.gympt.domain.reverseAuction.service.ReverseAuctionService;
import com.example.gympt.domain.trainer.service.TrainerService;
import com.example.gympt.security.MemberAuthDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainer") // 트레이너 권한 필요
@RequiredArgsConstructor
@Slf4j
public class TrainerController {

    private final TrainerService trainerService;
    private final ReverseAuctionService reverseAuctionService;

    //프래잰테이션 계층 (도메인 표현)
//트레이너 권한 회원만 이용할 수 있는 api


    //역경매 입찰 신청
    @PostMapping("/auction")
    public ResponseEntity<String> letAuctionTrainer(@AuthenticationPrincipal final MemberAuthDTO memberAuthDTO, @RequestBody TrainerAuctionRequestDTO trainerAuctionRequestDTO) {
        trainerService.applyAuction(memberAuthDTO.getUsername(), trainerAuctionRequestDTO);
        return ResponseEntity.ok("역경매 참여 완료 되었습니다");
    }

    //pt 가격 변경
    @PutMapping("/auction/update")
    public ResponseEntity<String> updateAuctionPrice(@AuthenticationPrincipal final MemberAuthDTO memberAuthDTO, @RequestBody Long auctionRequestId, @RequestBody Long updatePrice) {
        trainerService.changePrice(auctionRequestId, memberAuthDTO.getUsername(), updatePrice);
        return ResponseEntity.ok("가격 변경이 완료 되었습니다 ");
    }

//    //트레이너에게만 보여지는 역경매 신청 list
//    @GetMapping("/auction/list")
//    public ResponseEntity<List<AuctionResponseToTrainerDTO>> getAuctionRequestList() {
//        List<AuctionResponseToTrainerDTO> response = reverseAuctionService.getAuctionListToTrainers(String );
//        return ResponseEntity.ok().body(response);
//
//    }

//    //사용자가 신청한 역경매 정보 디테일
//    @GetMapping("/auction/{auctionId}")
//    public ResponseEntity<AuctionResponseToTrainerDTO> getAuctionDetailById(@PathVariable Long auctionId) {
//        AuctionResponseToTrainerDTO auctionResponseToTrainerDTO = reverseAuctionService.getAuctionToTrainer(auctionId);
//        return ResponseEntity.ok().body(auctionResponseToTrainerDTO);
//    }


}
