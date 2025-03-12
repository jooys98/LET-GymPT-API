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
public class ExcelController {

    private final TrainerExcelService trainerExcelService;
    private final GymExcelService excelService;
    private final MemberExcelService memberExcelService;

    @PostMapping("/register")
    public ResponseEntity<?> registerByExcel(
            @RequestPart(value = "file") MultipartFile batchRegistrationFile
    ) {

        List<CreateGymDTO> registrationDtoList = GymDataExcelExtractor.extract(batchRegistrationFile);
        Long registeredSize = excelService.register(registrationDtoList);
        return new ResponseEntity<>(registeredSize + "개 row 데이터 excel 등록 완료!", HttpStatus.CREATED);
    }


    @PostMapping("/register/member")
    public ResponseEntity<?> registerMembersByExcel(
            @RequestPart(value = "file") MultipartFile batchRegistrationFile
    ) {

        List<JoinRequestDTO> registrationDtoList = MemberDataExcelExtractor.extract(batchRegistrationFile);
        Long registeredSize = memberExcelService.register(registrationDtoList);
        return new ResponseEntity<>(registeredSize + "개 row 데이터 excel 등록 완료!", HttpStatus.CREATED);
    }


    @PostMapping("/register/trainer")
    public ResponseEntity<?> registerTrainersByExcel(
            @RequestPart(value = "file") MultipartFile batchRegistrationFile
    ) {
        log.info("엑셀 업로드 시작");
        List<TrainerSaveRequestDTO> registrationDtoList = TrainerDataExcelExtractor.extract(batchRegistrationFile);
        Long registeredSize = trainerExcelService.register(registrationDtoList);
        return new ResponseEntity<>(registeredSize + "개 row 데이터 excel 등록 완료!", HttpStatus.CREATED);
    }


}

