package com.example.gympt.domain.likes.controller;

import com.example.gympt.domain.likes.dto.LikesGymDTO;
import com.example.gympt.domain.likes.dto.LikesRequestDTO;
import com.example.gympt.domain.likes.dto.LikesTrainersDTO;
import com.example.gympt.domain.likes.service.LikesService;
import com.example.gympt.security.MemberAuthDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("api/likes")
@Tag(name = "likes API", description = "헬스장 및 트레이너 좋아요 추가/삭제, 좋아요한 헬스장 및 트레이너 조회 API 입니다")
public class LikesController {

    private final LikesService likesService;

    @Operation(summary = "헬스장 좋아요 추가/ 삭제 토글 api", description = "RequestBody 에 담긴 gymId로 좋아요 추가 , 삭제 서비스를 제공합니다. 좋아요 추가시 true , 취소시 false 가 response 로 전달됩니다.")
    @PostMapping("/gym")
    public ResponseEntity<Boolean> toggleGymLikes(@Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal MemberAuthDTO memberAuthDTO,
                                                  @Parameter(description = "gymId / trainerId 가 담긴 dto")
                                                  @RequestBody LikesRequestDTO likesRequestDTO) {
        Boolean gymLikes = likesService.toggleGymLikes(memberAuthDTO.getEmail(), likesRequestDTO.getGymId());
        return ResponseEntity.ok(gymLikes);
    }

    @Operation(summary = "사용자의 좋아요 누른 헬스장을 조회합니다")
    @GetMapping("/gym")
    public ResponseEntity<List<LikesGymDTO>> getLikesGymList(@Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal MemberAuthDTO memberAuthDTO) {
        List<LikesGymDTO> likesGymDTOList = likesService.getLikesGymList(memberAuthDTO.getEmail());
        return ResponseEntity.ok(likesGymDTOList);
    }

    @Operation(summary = "트레이너 좋아요 추가/ 삭제 토글 api", description = "RequestBody 에 담긴 gymId로 좋아요 추가 , 삭제 서비스를 제공합니다. 좋아요 추가시 true , 취소시 false 가 response 로 전달됩니다.")
    @PostMapping("/trainer")
    public ResponseEntity<Boolean> toggleTrainerLikes(@Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal MemberAuthDTO memberAuthDTO,
                                                      @Parameter(description = "gymId / trainerId 가 담긴 dto")@RequestBody LikesRequestDTO likesRequestDTO) {
        Boolean trainerLikes = likesService.toggleTrainerLikes(memberAuthDTO.getEmail(), likesRequestDTO.getTrainerId());
        return ResponseEntity.ok(trainerLikes);
    }

    @Operation(summary = "사용자의 좋아요 누른 트레이너를 조회합니다")
    @GetMapping("/trainer")
    public ResponseEntity<List<LikesTrainersDTO>> getLikesTrainerList(@Parameter(description = "인증된 사용자 정보") @AuthenticationPrincipal MemberAuthDTO memberAuthDTO) {
        List<LikesTrainersDTO> likesTrainersDTOList = likesService.getLikesTrainerList(memberAuthDTO.getEmail());
        return ResponseEntity.ok(likesTrainersDTOList);
    }
}
