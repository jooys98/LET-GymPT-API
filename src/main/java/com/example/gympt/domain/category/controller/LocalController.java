package com.example.gympt.domain.category.controller;

import com.example.gympt.domain.category.dto.LocalDTO;
import com.example.gympt.domain.category.dto.LocalParentDTO;
import com.example.gympt.domain.category.dto.LocalResponseDTO;
import com.example.gympt.domain.category.service.LocalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<LocalResponseDTO>> getGymsByLocal(@PathVariable Long localId) {
        return ResponseEntity.ok(localService.getLocalGymList(localId));
    }


    //모든 지역 보기
    @GetMapping
    public ResponseEntity<List<LocalDTO>> getAllLocal() {
        return ResponseEntity.ok(localService.getAll());
    }

//지역 아이디별 헬스장 보기
    @GetMapping("/list/sub/{localId}")
public ResponseEntity<List<LocalDTO>> getSubCategory(@PathVariable Long localId) {
        return ResponseEntity.ok(localService.getSubLocals(localId));
    }

    @GetMapping("/list/{localId}")
    public ResponseEntity<List<LocalParentDTO>> getAll(@PathVariable Long localId) {
        return ResponseEntity.ok(localService.getLocals(localId));
    }
}
