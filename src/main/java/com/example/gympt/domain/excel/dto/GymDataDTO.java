package com.example.gympt.domain.excel.dto;


import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class GymDataDTO {

    @ExcelColumn(header = "헬스장 아이디")
    private Long id;

    @ExcelColumn(header = "제목")
    private String gymName;

    @ExcelColumn(header = "하루 가격")
    private Long dailyPrice;

    @ExcelColumn(header = "한달 가격")
    private Long monthlyPrice;

    @ExcelColumn(header = "주소")
    private String address;

    @ExcelColumn(header = "설명")
    private String description;

    @ExcelColumn(header = "지역 아이디")
    private Long localId;

    @ExcelColumn(header = "이미지 경로 리스트")
    private List<String> imageList;


    @ExcelColumn(header = "인기여부")
    private String popular;
}
