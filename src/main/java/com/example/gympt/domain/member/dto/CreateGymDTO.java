package com.example.gympt.domain.member.dto;

import com.example.gympt.domain.gym.enums.Popular;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Entity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@ToString
public class CreateGymDTO {
//헬스장 등록,수정용 dto
    private String gymName;
    private String address;
    private String local;
    private String description;
    private Long dailyPrice;
    private Long monthlyPrice;
    private Popular popular;
    private List<MultipartFile> files;
    //폼데이타



}
