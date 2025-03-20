package com.example.gympt.domain.category.controller;

import com.example.gympt.domain.category.dto.LocalDTO;
import com.example.gympt.domain.category.dto.LocalParentDTO;
import com.example.gympt.domain.category.dto.LocalResponseDTO;
import com.example.gympt.domain.category.service.LocalService;
import com.example.gympt.domain.gym.dto.GymResponseDTO;
import com.example.gympt.security.MemberAuthDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "local-api", description = "최상위 지역 , 하위지역 , 최하위 지역에 해당하는 지역과 헬스장을 조회히는 api(3 depth)")
public class LocalController {

    private final LocalService localService;

    //지역별 헬스장 보기


    //최상위 지역
    @Operation(summary = "최상위 지역을 조회합니다", description = "파라미터 없이 최상위 지역들만 보여주는 api 입니다. ex) 관악구 , 강남구 , 영등포구")
    @GetMapping
    public ResponseEntity<List<LocalDTO>> getAllLocal() {
        return ResponseEntity.ok(localService.getAll());
    }

    //지역 아이디별 하위 카테고리 지역
    @Operation(summary = "최상위 지역 / 하위 지약에 해당하는 최하위 지역을 조회합니다", description = "지역 아이디를 파라미터로 받아서 이에 해당하는 지역들을 보여주는 api 입니다. ex) 관악구 - 봉천동 , 신림동")
    @GetMapping("/list/sub/{localId}")
    public ResponseEntity<List<LocalDTO>> getSubCategory(@Parameter(description = "local ID", required = true) @PathVariable Long localId) {
        return ResponseEntity.ok(localService.getSubLocals(localId));
    }

    //최상위 지역에 해당하는 모든 로칼 카테고리 전부 나오게
    @Operation(summary = "최상위 지역에 해당하는 지역들을 모두 조회합니다", description = "지역 아이디를 파라미터로 받아서 이에 해당하는 지역들을 보여주는 api 입니다. ex) 관악구 - 봉천동 , 신림동 - 서울대입구역 ")
    @GetMapping("/list/{localId}")
    public ResponseEntity<List<LocalParentDTO>> getAll(@PathVariable Long localId) {
        return ResponseEntity.ok(localService.getLocals(localId));
    }

    @Operation(summary = "admin 의 헬스장 선택 / 역경매 신청시 전체 지역 조회를 할수 있는 api 입니다.")
    @GetMapping("/local")
    public ResponseEntity<List<LocalDTO>> list() {
        List<LocalDTO> dto = localService.localList();
        return ResponseEntity.ok(dto);
    }

}
