package com.example.gympt.domain.trainer.controller;

import com.example.gympt.domain.trainer.dto.TrainerSaveRequestDTO;
import com.example.gympt.domain.trainer.service.TrainerService;
import com.example.gympt.dto.PageResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trainer") // 회원 권한 필요
@RequiredArgsConstructor
@Slf4j
public class TrainerController {

    private final TrainerService trainerService;
//프래잰테이션 계층 (도메인 표현)

    @PostMapping("/apply")
    //트레이너 신청!!!
    public ResponseEntity<String> trainerApply(@RequestBody TrainerSaveRequestDTO trainerSaveRequestDTO) {
        trainerService.saveTrainer(trainerSaveRequestDTO);
        return ResponseEntity.ok().body("트레이너 신청이 완료 되었습니댜!");
    }


}
