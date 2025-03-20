package com.example.gympt.domain.member.controller;

import com.example.gympt.domain.booking.dto.BookingRequestDTO;
import com.example.gympt.domain.category.dto.LocalDTO;
import com.example.gympt.domain.category.service.LocalService;
import com.example.gympt.domain.member.dto.CreateGymDTO;
import com.example.gympt.domain.member.dto.MemberResponseDTO;
import com.example.gympt.domain.member.dto.RoleChangeRequest;
import com.example.gympt.domain.member.service.AdminService;
import com.example.gympt.domain.trainer.dto.TrainerSaveFormDTO;
import com.example.gympt.domain.trainer.service.TrainerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin API", description = "관리자만 접근 가능한 API")
public class AdminController {

    private final AdminService adminService;
    private final LocalService localService;

    @Operation(
            summary = "트레이너 권한 변경 및 trainer tbl insert ",
            description = "트레이너의 이메일이 담긴 dto 를 받아서 트레이너 권한 부여 및 trainer tbl 에 저장",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "트레이너 신청을 한 회원의 이메일이 담긴 dto 입니다",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RoleChangeRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "트레이너 권한 변경 및 insert 성공")
            }
    )
    @PostMapping("/changed-role")
    public ResponseEntity<String> changedRole(
            @RequestBody RoleChangeRequest roleChangeRequest) {
        adminService.approveTrainer(roleChangeRequest.getEmail());
        return ResponseEntity.ok("트레이너 데뷔를 축하드립니다");
    }

    @Operation(
            summary = "여러 트레이너 권한 변경 및 trainer tbl insert ",
            description = "트레이너의 이메일이 담긴 dto 를 배열로 받아서 트레이너 권한 부여 및 trainer tbl 에 저장",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "트레이너 신청을 한 회원의 이메일이 담긴 dto 입니다",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RoleChangeRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "트레이너 권한 변경 및 insert 성공")
            }
    )
    @PostMapping("/changed-role-list")
    public ResponseEntity<?> changedRoleList(
            @RequestBody List<RoleChangeRequest> roleChangeRequests) {
        //결과를 담기 위한 객체들
        Map<String, Object> result = new HashMap<>();
        List<String> successList = new ArrayList<>(); //성공한 이메일 목록
        List<String> failList = new ArrayList<>();
        Map<String, String> errors = new HashMap<>(); //에러를 보내기 위한 해쉬맵

        // 각 요청에 대해 처리
        for (RoleChangeRequest request : roleChangeRequests) {
            try {
                adminService.approveTrainer(request.getEmail());
                successList.add(request.getEmail());
            } catch (Exception e) {
                failList.add(request.getEmail());
                errors.put(request.getEmail(), e.getMessage());
            }
        }

        // 결과 정보 구성
        result.put("successCount", successList.size());
        result.put("failCount", failList.size());
        result.put("successList", successList);

        if (!failList.isEmpty()) {
            result.put("failList", failList);
            result.put("errors", errors);
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body(result);
        }

        return ResponseEntity.ok(result);
    }


    @Operation(summary = "트레이너 신청 목록 조회 ", description = "트레이너 신청한 예비 트레이너들의 정보를 조회 합니다 ")
    @GetMapping("/pending-list")
    public ResponseEntity<List<TrainerSaveFormDTO>> getTrainersSaveForm() {
        List<TrainerSaveFormDTO> preparationTrainerList = adminService.getPreparationTrainers();
        return ResponseEntity.ok(preparationTrainerList);
    }



    @Operation(summary = "회원 목록 조회 ", description = "Let gymPT에 가입되어 있는 모든 회원을 조회합니다. ")
    @GetMapping("/members")
    public ResponseEntity<List<MemberResponseDTO>> getAllMembers() {
        List<MemberResponseDTO> memberList = adminService.getAllMembers();
        return ResponseEntity.ok(memberList);
    }

    @Operation(
            summary = "헬스장 등록하기 ",
            description = "등록할 헬스장의 정보가 담긴 dto 를 받아서 gym_tbl 에 insert 합니다. ",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "등록할 헬스장의 정보들을 담은 dto 이며, body 에 form-data 형식으로 전달됩니다 ",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateGymDTO.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "헬스장 등록 성공")
            }
    )
    @PostMapping("/gym")
    public ResponseEntity<String> createGym(CreateGymDTO createGymDTO) {
        adminService.createGym(createGymDTO);
        return ResponseEntity.ok("헬스장 등록이 완료 되었습니다 ");
    }

    @Operation(summary = "헬스장 삭제 ", description = "헬스장의 id를 파라미터로 받아 해당 헬스장을 삭제합니다. 헬스장에 소속되어 있는 트레이너들은 임시 헬스장에 소속되게 변경합니다.")
    @DeleteMapping("/gym/{gymId}")
    public ResponseEntity<Long> deleteGym(@PathVariable Long gymId) {
        return ResponseEntity.ok(adminService.deleteGym(gymId));
    }

    @Operation(
            summary = "헬스장 수정하기",
            description = "헬스장의 수정 내용을 파라미터로 받아서 헬스장의 정보를 수정합니다",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "수정 예정인 헬스장의 정보가 담긴 dto 입니다. ",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateGymDTO.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "예약하기 성공")
            }
    )
    @PutMapping("gym/{gymId}")
    public ResponseEntity<Long> updateGym(@PathVariable Long gymId,
                                          CreateGymDTO createGymDTO) {
        return ResponseEntity.ok(adminService.updateGym(gymId, createGymDTO));
    }

}
