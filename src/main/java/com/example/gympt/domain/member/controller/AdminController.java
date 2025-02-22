package com.example.gympt.domain.member.controller;

import com.example.gympt.domain.member.dto.CreateGymDTO;
import com.example.gympt.domain.member.service.AdminService;
import com.example.gympt.domain.trainer.dto.TrainerSaveFormDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/changed/role")
    public ResponseEntity<String> changedRole(
            @RequestParam String trainerEmail,
            @AuthenticationPrincipal final UserDetails adminDetails) {
        //관리자 권한으로 트레이너 권한 변경과 동시에 트레이너 테이블에 추가
        adminService.approveTrainer(trainerEmail, adminDetails.getUsername());
        return ResponseEntity.ok("트레이너 데뷔를 축하드립니다");
    }

    //트레이너 신청 목록 리스트 보기
    @GetMapping("/get/trainers/pending/list")
    public ResponseEntity<List<TrainerSaveFormDTO>> getTrainersSaveForm(@AuthenticationPrincipal final UserDetails adminDetails) {
        List<TrainerSaveFormDTO> preparationTrainerList = adminService.getPreparationTrainers(adminDetails.getUsername());
        return ResponseEntity.ok(preparationTrainerList);
    }

    //헬스장이 먼저 등록됨
//이후에 새로 생긴 트레이너가 해당 헬스장과 연결됨
//리뷰는 사용자들이 이용 후에 작성함
//좋아요는 사용자들이 나중에 추가함

    @PostMapping("/create/gym")
    public ResponseEntity<String> createGym(CreateGymDTO createGymDTO,
                                            @AuthenticationPrincipal final UserDetails adminDetails) {
        adminService.createGym(createGymDTO, adminDetails.getUsername());
        return ResponseEntity.ok("헬스장 등록이 완료 되었습니다 ");
    }

    @DeleteMapping("/delete/gym/{gymId}")
    public ResponseEntity<String> deleteGym(@PathVariable Long gymId,
                                            @AuthenticationPrincipal final UserDetails adminDetails) {
        adminService.deleteGym(gymId, adminDetails.getUsername());
        return ResponseEntity.ok("헬스장 삭제 완료 되었숩니다");
    }

    //헬스장 수정!!
    @PutMapping("update/gym/{gymId}")
    public ResponseEntity<String> updateGym(@PathVariable Long gymId,
                                            CreateGymDTO createGymDTO,
                                            @AuthenticationPrincipal final UserDetails adminDetails) {
        adminService.updateGym(gymId, createGymDTO, adminDetails.getUsername());
        return ResponseEntity.ok("헬스장 수정이 완료 되었습니다!");
    }

}
