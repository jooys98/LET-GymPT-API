package com.example.gympt.domain.likes.controller;

import com.example.gympt.domain.likes.dto.LikesGymDTO;
import com.example.gympt.domain.likes.dto.LikesRequestDTO;
import com.example.gympt.domain.likes.dto.LikesTrainersDTO;
import com.example.gympt.domain.likes.service.LikesService;
import com.example.gympt.security.MemberAuthDTO;
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
public class LikesController {

    private final LikesService likesService;

    //헬스장 좋아요 추가 / 취소
    @PostMapping("/gym")
    public ResponseEntity<Boolean> toggleGymLikes(@AuthenticationPrincipal MemberAuthDTO memberAuthDTO, @RequestBody LikesRequestDTO likesRequestDTO) {
        Boolean gymLikes = likesService.toggleGymLikes(memberAuthDTO.getEmail(), likesRequestDTO.getGymId());
        return ResponseEntity.ok(gymLikes);
    }

    //졿아요 누른 헬스장 조회
    @GetMapping("/gym/list")
    public ResponseEntity<List<LikesGymDTO>> getLikesGymList(@AuthenticationPrincipal MemberAuthDTO memberAuthDTO) {
        List<LikesGymDTO> likesGymDTOList = likesService.getLikesGymList(memberAuthDTO.getEmail());
        return ResponseEntity.ok(likesGymDTOList);
    }

    //트레이너 좋아요 추가 / 취소
    @PostMapping("/trainer")
    public ResponseEntity<Boolean> toggleTrainerLikes(@AuthenticationPrincipal MemberAuthDTO memberAuthDTO, @RequestBody LikesRequestDTO likesRequestDTO) {
        Boolean trainerLikes = likesService.toggleTrainerLikes(memberAuthDTO.getEmail(), likesRequestDTO.getTrainerId());
        return ResponseEntity.ok(trainerLikes);
    }

    //좋아요 누른 트레이너 조회
    @GetMapping("/trainer/list")
    public ResponseEntity<List<LikesTrainersDTO>> getLikesTrainerList(@AuthenticationPrincipal MemberAuthDTO memberAuthDTO) {
        List<LikesTrainersDTO> likesTrainersDTOList = likesService.getLikesTrainerList(memberAuthDTO.getEmail());
        return ResponseEntity.ok(likesTrainersDTOList);
    }
}
