package com.example.gympt.domain.category.controller;

import com.example.gympt.domain.category.dto.LocalDTO;
import com.example.gympt.domain.category.dto.LocalParentDTO;
import com.example.gympt.domain.category.dto.LocalResponseDTO;
import com.example.gympt.domain.category.service.LocalService;
import com.example.gympt.domain.gym.dto.GymResponseDTO;
import com.example.gympt.security.MemberAuthDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/local")

public class LocalController {

    private final LocalService localService;

    //지역별 헬스장 보기
    @GetMapping("/gyms/{localId}")
    public ResponseEntity<List<GymResponseDTO>> getGymsByLocal(@PathVariable Long localId, @AuthenticationPrincipal final MemberAuthDTO memberDTO) {
        String email = (memberDTO != null) ? memberDTO.getEmail() : null;
        return ResponseEntity.ok(localService.getLocalGymList(localId, email));
    }

    //최상위 지역
    @GetMapping
    public ResponseEntity<List<LocalDTO>> getAllLocal() {
        return ResponseEntity.ok(localService.getAll());
    }

    //지역 아이디별 하위 카테고리 지역
    @GetMapping("/list/sub/{localId}")
    public ResponseEntity<List<LocalDTO>> getSubCategory(@PathVariable Long localId) {
        return ResponseEntity.ok(localService.getSubLocals(localId));
    }

    //최상위 지역에 해당하는 모든 로칼 카테고리 전부 나오게
    @GetMapping("/list/{localId}")
    public ResponseEntity<List<LocalParentDTO>> getAll(@PathVariable Long localId) {
        return ResponseEntity.ok(localService.getLocals(localId));
    }
}
