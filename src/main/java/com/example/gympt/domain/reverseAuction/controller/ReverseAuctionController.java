package com.example.gympt.domain.reverseAuction.controller;

import com.example.gympt.domain.member.entity.Member;
import com.example.gympt.domain.member.enums.MemberRole;
import com.example.gympt.domain.reverseAuction.dto.*;
import com.example.gympt.domain.reverseAuction.service.ReverseAuctionService;
import com.example.gympt.security.MemberAuthDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reverse-auction")
@Tag(name = "Reverse Auction API", description = "역경매 관련 API")
public class ReverseAuctionController {

    private final ReverseAuctionService reverseAuctionService;

    @Operation(summary = "역경매 신청", description = "새로운 역경매를 신청합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 신청됨",
                    content = @Content(schema = @Schema(type = "integer", format = "int64"))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/apply")
    public ResponseEntity<Long> applyReverseAuction(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "역경매 신청 정보", required = true)
                                                    @RequestBody AuctionRequestDTO auctionRequestDTO) {
        return ResponseEntity.ok(reverseAuctionService.applyAuction(auctionRequestDTO));
    }


    @Operation(summary = "트레이너 낙찰", description = "역경매에 참여한 트레이너를 최종 선택합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 트레이너 선택됨"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "해당 트레이너 또는 경매 찾을 수 없음")})
    @PostMapping("/select")
    public ResponseEntity<FinalSelectAuctionDTO> selectFinalTrainer(@Parameter(description = "인증된 회원 정보", required = true)
                                                                    @AuthenticationPrincipal final MemberAuthDTO memberAuthDTO,
                                                                    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "선택된 트레이너 정보", required = true)
                                                                    @RequestBody SelectedTrainerDTO selectedTrainerDTO) {
        FinalSelectAuctionDTO finalSelectAuctionDTO = reverseAuctionService.selectTrainer(memberAuthDTO.getEmail(), selectedTrainerDTO.getTrainerId());
        return ResponseEntity.ok(finalSelectAuctionDTO);
    }

    @Operation(summary = "역경매 참여 트레이너 목록", description = "특정 역경매에 참여한 트레이너 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 목록 조회됨"),
            @ApiResponse(responseCode = "404", description = "해당 경매 찾을 수 없음")
    })
    @GetMapping("/trainers/{auctionRequestId}")
    public ResponseEntity<List<AuctionTrainerBidResponseDTO>> getTrainersInAuction(@Parameter(description = "역경매 ID", required = true)
                                                                                   @PathVariable Long auctionRequestId) {
        return ResponseEntity.ok(reverseAuctionService.getTrainers(auctionRequestId));
    }


    @Operation(summary = "역경매 목록 조회", description = "로그인한 사용자 또는 트레이너가 볼 수 있는 역경매 목록을 조회합니다. 민감한 정보는 트레이너 권한을 가진 사용자에게만 보여집니다. ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 목록 조회됨"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @GetMapping("/list")
    public ResponseEntity<List<?>> getAuctionList(@Parameter(description = "인증된 회원 정보", required = true)
                                                  @AuthenticationPrincipal final MemberAuthDTO memberAuthDTO) {

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

    @Operation(summary = "역경매 목록 조회", description = "로그인한 사용자 또는 트레이너가 볼 수 있는 역경매 목록을 조회합니다. 민감한 정보는 트레이너 권한을 가진 사용자에게만 보여집니다. ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 목록 조회됨"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @GetMapping("/{auctionRequestId}")
    public ResponseEntity<?> getAuctionDetailById(@Parameter(description = "인증된 회원 정보", required = true)
                                                  @AuthenticationPrincipal final MemberAuthDTO memberAuthDTO,
                                                  @Parameter(description = "역경매 ID", required = true)
                                                  @PathVariable Long auctionRequestId) {
        Object auctionResponseDTO = reverseAuctionService.getAuction(auctionRequestId, memberAuthDTO.getEmail());
        return ResponseEntity.ok(auctionResponseDTO);
    }

    @Operation(summary = "지역별 역경매 조회", description = "특정 지역의 역경매 목록을 조회합니다.마찬가지로 민감한 정보는 트레이너 권한을 가진 사용자에게만 보여집니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 목록 조회됨"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "해당 지역 찾을 수 없음")
    })
    @GetMapping("/local/{localId}")
    public ResponseEntity<List<?>> getLocalAuction(@Parameter(description = "인증된 회원 정보", required = true)
                                                   @AuthenticationPrincipal final MemberAuthDTO memberAuthDTO,
                                                   @Parameter(description = "지역 ID", required = true)
                                                   @PathVariable Long localId) {
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

    @Operation(summary = "역경매 취소", description = "사용자가 등록한 역경매를 취소합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 취소됨"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "해당 경매 찾을 수 없음")
    })


    @DeleteMapping("/{auctionRequestId}")
    public ResponseEntity<Long> deleteAuction(@Parameter(description = "인증된 회원 정보", required = true)
                                              @AuthenticationPrincipal final MemberAuthDTO memberAuthDTO,
                                              @Parameter(description = "역경매 ID", required = true)
                                              @PathVariable Long auctionRequestId) {
        return ResponseEntity.ok(reverseAuctionService.cancelAuction(memberAuthDTO.getEmail(), auctionRequestId));
    }

    @Operation(summary = "매칭된 역경매 내역 조회", description = "회원이 매칭된 역경매 내역을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 내역 조회됨"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })


    @GetMapping("/history")
    public ResponseEntity<List<FinalSelectAuctionDTO>> getHistory(@Parameter(description = "인증된 회원 정보", required = true)
                                                                  @AuthenticationPrincipal final MemberAuthDTO memberAuthDTO) {
        return ResponseEntity.ok(reverseAuctionService.getAuctionHistory(memberAuthDTO.getEmail()));
    }

}
