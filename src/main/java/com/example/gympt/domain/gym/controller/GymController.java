package com.example.gympt.domain.gym.controller;

import com.example.gympt.domain.gym.dto.GymResponseDTO;
import com.example.gympt.domain.gym.dto.GymSearchRequestDTO;
import com.example.gympt.domain.gym.service.GymService;
import com.example.gympt.domain.trainer.dto.TrainerRequestDTO;
import com.example.gympt.domain.trainer.dto.TrainerResponseDTO;
import com.example.gympt.domain.trainer.service.TrainerService;
import com.example.gympt.dto.PageRequestDTO;
import com.example.gympt.dto.PageResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gym") // 회원 권한 필요 없이 헬스장과 트레이너를 조회 할 수 있는 api
@RequiredArgsConstructor
public class GymController {

    private final GymService gymService;
    private final TrainerService trainerService;
//인증 처리가 필요없는 gym - trainer 조회 관련 로직은 전부 이곳에서 처리!
//    @GetMapping("/list")
//    public ResponseEntity<List<GymResponseDTO>> SearchGyms(GymSearchRequestDTO gymSearchRequestDTO) {
//        List<GymResponseDTO> gymResponseDTOS = gymService.getGyms(gymSearchRequestDTO);
//        return ResponseEntity.ok(gymResponseDTOS);
//    }

    @GetMapping("/list") //@ModelAttribute : 클라이언트가 요청한 파라미터 값만 전달하여 결과를 보여줌 , dto 요소에 나머지 값들은 null 처리
    //헬스장 목록 다중조건 조회 + 페이지네이션
    public ResponseEntity<PageResponseDTO<GymResponseDTO>> SearchGyms(@ModelAttribute GymSearchRequestDTO gymSearchRequestDTO,
                                                                      @ModelAttribute PageRequestDTO pageRequestDTO) {
        PageResponseDTO<GymResponseDTO> gymResponseDTOS = gymService.getGyms(gymSearchRequestDTO, pageRequestDTO);
        return ResponseEntity.ok(gymResponseDTOS);
    }

    @GetMapping("/trainer/list")
    //트레이너 목록 다중조건 조회 + 페이지 네이션
    public ResponseEntity<PageResponseDTO<TrainerResponseDTO>> listTrainers(@ModelAttribute TrainerRequestDTO trainerRequestDTO,
                                                                            @ModelAttribute PageRequestDTO pageRequestDTO) {
        PageResponseDTO<TrainerResponseDTO> trainerResponseDTOS = trainerService.getTrainers(trainerRequestDTO, pageRequestDTO);
        return ResponseEntity.ok(trainerResponseDTOS);
    }

    @GetMapping("detail/{id}")
    //헬스장 상세보기
    public ResponseEntity<GymResponseDTO> detailGym(@PathVariable Long id) {
        GymResponseDTO gymResponseDTO =  gymService.getGymById(id);
        return ResponseEntity.ok(gymResponseDTO);
    }

    @GetMapping("trainer/detail/{id}")
    //트레이너 상세보기
private ResponseEntity<TrainerResponseDTO> detailTrainer(@PathVariable Long id) {
        TrainerResponseDTO trainerResponseDTO = trainerService.getTrainerById(id);
        return ResponseEntity.ok(trainerResponseDTO);
    }


}



