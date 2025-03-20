package com.example.gympt.domain.excel.controller;

import com.example.gympt.domain.excel.extractor.GymDataExcelExtractor;
import com.example.gympt.domain.excel.extractor.MemberDataExcelExtractor;
import com.example.gympt.domain.excel.extractor.TrainerDataExcelExtractor;
import com.example.gympt.domain.excel.service.GymExcelService;
import com.example.gympt.domain.excel.service.MemberExcelService;
import com.example.gympt.domain.excel.service.TrainerExcelService;
import com.example.gympt.domain.member.dto.CreateGymDTO;
import com.example.gympt.domain.member.dto.JoinRequestDTO;
import com.example.gympt.domain.trainer.dto.TrainerSaveRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/excel")
@Tag(name = "Excel API", description = "엑셀을 통한 대량 데이터 등록 API")
public class ExcelController {

    private final TrainerExcelService trainerExcelService;
    private final GymExcelService excelService;
    private final MemberExcelService memberExcelService;


    @Operation(
            summary = "헬스장 데이터 엑셀 등록",
            description = "엑셀 파일을 업로드하여 헬스장 데이터를 대량 등록합니다."
    )
    @ApiResponse(responseCode = "201", description = "엑셀 데이터 등록 성공")
    @PostMapping("/gym")
    public ResponseEntity<?> registerByExcel(@Parameter(
            description = "업로드할 Excel 파일",
            required = true,
            content = @Content(mediaType = "application/octet-stream")
    )
            @RequestPart(value = "file") MultipartFile batchRegistrationFile
    ) {

        List<CreateGymDTO> registrationDtoList = GymDataExcelExtractor.extract(batchRegistrationFile);
        Long registeredSize = excelService.register(registrationDtoList);
        return new ResponseEntity<>(registeredSize + "개 row 데이터 excel 등록 완료!", HttpStatus.CREATED);
    }

    @Operation(
            summary = "회원 데이터 엑셀 등록",
            description = "엑셀 파일을 업로드하여 회원 데이터를 대량 등록합니다."
    )
    @ApiResponse(responseCode = "201", description = "회원 데이터 등록 성공")
    @PostMapping("/member")
    public ResponseEntity<?> registerMembersByExcel(@Parameter(
            description = "업로드할 Excel 파일",
            required = true,
            content = @Content(mediaType = "application/octet-stream")
    )
            @RequestPart(value = "file") MultipartFile batchRegistrationFile
    ) {

        List<JoinRequestDTO> registrationDtoList = MemberDataExcelExtractor.extract(batchRegistrationFile);
        Long registeredSize = memberExcelService.register(registrationDtoList);
        return new ResponseEntity<>(registeredSize + "개 row 데이터 excel 등록 완료!", HttpStatus.CREATED);
    }

    @Operation(
            summary = "트레이너 데이터 엑셀 등록",
            description = "엑셀 파일을 업로드하여 회원 데이터를 대량 등록합니다."
    )
    @ApiResponse(responseCode = "201", description = "회원 데이터 등록 성공")
    @PostMapping("/trainer")
    public ResponseEntity<?> registerTrainersByExcel(@Parameter(
            description = "업로드할 Excel 파일",
            required = true,
            content = @Content(mediaType = "application/octet-stream")
    )
            @RequestPart(value = "file") MultipartFile batchRegistrationFile
    ) {
        log.info("엑셀 업로드 시작");
        List<TrainerSaveRequestDTO> registrationDtoList = TrainerDataExcelExtractor.extract(batchRegistrationFile);
        Long registeredSize = trainerExcelService.register(registrationDtoList);
        return new ResponseEntity<>(registeredSize + "개 row 데이터 excel 등록 완료!", HttpStatus.CREATED);
    }


}

