package com.example.gympt.domain.gym.controller;

import com.example.gympt.domain.category.service.LocalService;
import com.example.gympt.domain.gym.dto.GymResponseDTO;
import com.example.gympt.domain.gym.dto.GymSearchRequestDTO;
import com.example.gympt.domain.gym.service.GymService;
import com.example.gympt.domain.trainer.dto.TrainerRequestDTO;
import com.example.gympt.domain.trainer.dto.TrainerResponseDTO;
import com.example.gympt.domain.trainer.service.TrainerService;
import com.example.gympt.dto.PageRequestDTO;
import com.example.gympt.dto.PageResponseDTO;
import com.example.gympt.security.MemberAuthDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gym")
@RequiredArgsConstructor
@Tag(name = "Gym & Trainer API", description = "회원 권한 필요 없이 헬스장 및 트레이너 조회 API 입니다")
public class GymController {

    private final GymService gymService;
    private final TrainerService trainerService;
    private final LocalService localService;

    @Operation(summary = "헬스장 목록 조회", description = "다중 조건을 사용하여 헬스장 목록을 조회하고 페이지네이션을 제공합니다.")
    @GetMapping("/list") //@ModelAttribute : 클라이언트가 요청한 파라미터 값만 전달하여 결과를 보여줌 , dto 요소에 나머지 값들은 null 처리
    public ResponseEntity<PageResponseDTO<GymResponseDTO>> gymList(@Parameter(description = "헬스장 검색 조건") @ModelAttribute GymSearchRequestDTO gymSearchRequestDTO,
                                                                   @Parameter(description = "페이지네이션 요청 정보") @ModelAttribute PageRequestDTO pageRequestDTO,
                                                                   @Parameter(description = "인증된 사용자 정보 , 좋아요를 누른 헬스장이면 Response 의  likes 필드가 true 로 표기됩니다", hidden = true)
                                                                   @AuthenticationPrincipal final MemberAuthDTO memberDTO) {
        String email = (memberDTO != null) ? memberDTO.getEmail() : null;
        PageResponseDTO<GymResponseDTO> gymResponseDTOS = gymService.getGyms(gymSearchRequestDTO, pageRequestDTO, email);
        return ResponseEntity.ok(gymResponseDTOS);
    }

    @Operation(summary = "트레이너 목록 조회", description = "다중 조건을 사용하여 트레이너 목록을 조회하고 페이지네이션을 제공합니다.")
    @GetMapping("/trainer-list")
    public ResponseEntity<PageResponseDTO<TrainerResponseDTO>> trainerList(@Parameter(description = "헬스장 검색 조건") @ModelAttribute TrainerRequestDTO trainerRequestDTO,
                                                                           @Parameter(description = "페이지네이션 요청 정보") @ModelAttribute PageRequestDTO pageRequestDTO,
                                                                           @Parameter(description = "인증된 사용자 정보 , 좋아요를 누른 트레이너 일 경우 Response 의  likes 필드가 true 로 표기됩니다", hidden = true)
                                                                           @AuthenticationPrincipal final MemberAuthDTO memberDTO) {
        String email = (memberDTO != null) ? memberDTO.getEmail() : null;
        PageResponseDTO<TrainerResponseDTO> trainerResponseDTOS = trainerService.getTrainers(trainerRequestDTO, pageRequestDTO, email);

        return ResponseEntity.ok(trainerResponseDTOS);
    }

    @Operation(summary = "헬스장 상세 조회", description = "ID에 해당하는 헬스장의 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<GymResponseDTO> detailGym(@Parameter(description = "헬스장 ID", required = true) @PathVariable Long id,
                                                    @Parameter(description = "인증된 사용자 정보 , 좋아요를 누른 헬스장이면 Response 의  likes 필드가 true 로 표기됩니다", hidden = true)
                                                    @AuthenticationPrincipal final MemberAuthDTO memberDTO) {
        String email = (memberDTO != null) ? memberDTO.getEmail() : null;
        GymResponseDTO gymResponseDTO = gymService.getGymById(id, email);
        return ResponseEntity.ok(gymResponseDTO);
    }

    @Operation(summary = "지역별 헬스장을 조회합니다", description = "지역의 아이디(하위지역 또는 최하위 지역 )를 파라미터로 받아서 지역에 해당하는 헬스장들을 조회합니다")
    @GetMapping("/gyms/{localId}")
    public ResponseEntity<List<GymResponseDTO>> getGymsByLocal(@Parameter(description = "local ID", required = true) @PathVariable Long localId, @Parameter(description = "인증된 사용자 정보", hidden = true) @AuthenticationPrincipal final MemberAuthDTO memberDTO) {
        String email = (memberDTO != null) ? memberDTO.getEmail() : null;
        return ResponseEntity.ok(localService.getLocalGymList(localId, email));
    }

    @Operation(summary = "트레이너 상세 조회", description = "ID에 해당하는 트레이너의 상세 정보를 조회합니다.")
    @GetMapping("/trainer/{id}")
    public ResponseEntity<TrainerResponseDTO> detailTrainer(@Parameter(description = "트레이너 ID", required = true) @PathVariable Long id,
                                                            @Parameter(description = "인증된 사용자 정보 , 좋아요를 누른 트레이너라면 Response 의  likes 필드가 true 로 표기됩니다", hidden = true)
                                                            @AuthenticationPrincipal final MemberAuthDTO memberDTO) {
        String email = (memberDTO != null) ? memberDTO.getEmail() : null;
        TrainerResponseDTO trainerResponseDTO = trainerService.getTrainerById(id, email);
        return ResponseEntity.ok(trainerResponseDTO);
    }

    @Operation(summary = "헬스장 별 트레이너 목록 조회", description = "특정 헬스장의 트레이너 목록을 조회합니다.")
    @GetMapping("/trainer-list/{id}")
    public ResponseEntity<List<TrainerResponseDTO>> trainerListByGym(@Parameter(description = "인증된 사용자 정보 , 좋아요를 누른 트레이너라면 Response 의  likes 필드가 true 로 표기됩니다", hidden = true)
                                                                     @AuthenticationPrincipal final MemberAuthDTO memberDTO, @PathVariable Long id) {
        String email = (memberDTO != null) ? memberDTO.getEmail() : null;
        List<TrainerResponseDTO> trainerResponseDTOS = trainerService.getTrainerByGymId(id, email);

        return ResponseEntity.ok(trainerResponseDTOS);
    }

    @Operation(summary = "지역별 트레이너 목록 조회", description = "특정 지역에 속한 트레이너 목록을 조회합니다.")
    @GetMapping("/trainer/local/{localId}")
    public ResponseEntity<List<TrainerResponseDTO>> getTrainersInLocal(@Parameter(description = "인증된 사용자 정보 , 좋아요를 누른 트레이너라면 Response 의  likes 필드가 true 로 표기됩니다", hidden = true)
                                                                       @AuthenticationPrincipal final MemberAuthDTO memberDTO,
                                                                       @Parameter(description = "지역 ID", required = true)
                                                                       @PathVariable Long localId) {
        return ResponseEntity.ok(trainerService.getTrainerListByLocal(memberDTO.getEmail(), localId));
    }
}



