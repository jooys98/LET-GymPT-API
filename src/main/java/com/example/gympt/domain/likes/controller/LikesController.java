package com.example.gympt.domain.likes.controller;

import com.example.gympt.domain.likes.dto.LikesGymDTO;
import com.example.gympt.domain.likes.dto.LikesTrainersDTO;
import com.example.gympt.domain.likes.service.LikesService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("api/likes")
public class LikesController {

    private final LikesService likesService;
//헬스장 좋아요 추가 / 취소
    @PostMapping("/gym/change")
    public ResponseEntity<String> toggleGymLikes(@RequestParam String email, @RequestParam Long gymId) {
        Boolean gymLikes = likesService.toggleGymLikes(email, gymId);
        return ResponseEntity.ok(gymLikes ? "좋아요 추가" : "좋아요 취소");
    }
//졿아요 누른 헬스장 조회
    @GetMapping("/gym/list")
    public ResponseEntity<List<LikesGymDTO>> getLikesGymList(@RequestParam String email) {
        List<LikesGymDTO> likesGymDTOList = likesService.getLikesGymList(email);
        return ResponseEntity.ok(likesGymDTOList);
    }
//트레이너 좋아요 추가 / 취소
    @PostMapping("/trainer/change")
    public ResponseEntity<String> toggleTrainerLikes(@RequestParam String email, @RequestParam  String trainerEmail) {
        Boolean trainerLikes = likesService.toggleTrainerLikes(email, trainerEmail);
        return ResponseEntity.ok(trainerLikes ? "좋아요 추가" : "좋아요 취소");
    }
//좋아요 누른 트레이너 조회
    @GetMapping("/trainer/list")
    public ResponseEntity<List<LikesTrainersDTO>> getLikesTrainerList(@RequestParam String email) {
        List<LikesTrainersDTO> likesTrainersDTOList = likesService.getLikesTrainerList(email);
        return ResponseEntity.ok(likesTrainersDTOList);
    }
}
