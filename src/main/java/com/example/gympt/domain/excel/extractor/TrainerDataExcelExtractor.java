package com.example.gympt.domain.excel.extractor;

import com.example.gympt.domain.excel.util.ExcelDataExtractor;
import com.example.gympt.domain.member.dto.CreateGymDTO;
import com.example.gympt.domain.trainer.dto.TrainerSaveRequestDTO;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrainerDataExcelExtractor {

    public static List<TrainerSaveRequestDTO> extract(MultipartFile file) {
        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(file.getBytes()))) {
            ExcelDataExtractor<TrainerSaveRequestDTO> extractor = getExtractor();
            return extractor.extract(workbook.getSheetAt(0));
        } catch (IOException e) {
            throw new RuntimeException("엑셀 파일을 읽는 중 오류가 발생했습니다.", e);
        }
    }

    private static ExcelDataExtractor<TrainerSaveRequestDTO> getExtractor() {
        return new ExcelDataExtractor<TrainerSaveRequestDTO>() {
            private final DataFormatter dataFormatter = new DataFormatter();

            @Override
            protected TrainerSaveRequestDTO map(Row row) {
                TrainerSaveRequestDTO dto = TrainerSaveRequestDTO.builder()
                        .id((long) row.getCell(0).getNumericCellValue())
                        .email(row.getCell(1).getStringCellValue().trim())
                        .name(row.getCell(2).getStringCellValue().trim())
                        .introduction(row.getCell(3).getStringCellValue().trim())
                        .age((long) row.getCell(4).getNumericCellValue())
                        .gymName(row.getCell(5).getStringCellValue().trim())
                        .gymId((long) row.getCell(6).getNumericCellValue())
                        .localName(row.getCell(7).getStringCellValue().trim())
                        .localId((long) row.getCell(8).getNumericCellValue())
                        .images(getExcelImageList(row.getCell(9).getStringCellValue().trim()))
                        .profileImageUrl(row.getCell(10).getStringCellValue().trim())
                        .build();

                return dto;
            }
        };
    }

    /**
     * 이미지 경로 정보를 ","로 구분하여 List로 반환
     *
     * @param imagePathInfo 이미지 경로 정보
     * @return 이미지 경로 List
     */
    private static List<String> getExcelImageList(String imagePathInfo) {
        List<String> imageList = new ArrayList<>();
        // if "," 없을시
        if (imagePathInfo == null || imagePathInfo.isEmpty()) {
            return imageList;
        }
        if (!imagePathInfo.contains(",")) {
            // 이미지 경로가 1개일 경우
            imageList.add(imagePathInfo);
        } else {
            String[] imagePaths = imagePathInfo.split(",");
            imageList.addAll(Arrays.asList(imagePaths));
        }

        return imageList;
    }




}
