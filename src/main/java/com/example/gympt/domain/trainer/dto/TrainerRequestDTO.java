package com.example.gympt.domain.trainer.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class TrainerRequestDTO {
    private Long localId;
    private String localName;        // 지역으로 검색
    private String gymName;      // 헬스장 이름으로 검색
    private String gender;       // 성별
    private Long minAge;         // 나이 범위 검색
    private Long maxAge;
    private String name;
    private String searchKeyword;

    // 파일 입력값
//    @Builder.Default
//    private List<MultipartFile> files = new ArrayList<>();


}
//TODO : s3 이미지 이름 -> trinerSaveform 에도 적용시키기 , 버킷 만들기

