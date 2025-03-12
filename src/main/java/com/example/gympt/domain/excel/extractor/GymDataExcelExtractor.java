package com.example.gympt.domain.excel.extractor;


import com.example.gympt.domain.excel.util.ExcelDataExtractor;
import com.example.gympt.domain.member.dto.CreateGymDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class GymDataExcelExtractor {

    public static List<CreateGymDTO> extract(MultipartFile file) {
        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(file.getBytes()))) {
            ExcelDataExtractor<CreateGymDTO> extractor = getExtractor();
            return extractor.extract(workbook.getSheetAt(0));
        } catch (IOException e) {
            throw new RuntimeException("엑셀 파일을 읽는 중 오류가 발생했습니다.", e);
        }
    }

    private static ExcelDataExtractor<CreateGymDTO> getExtractor() {
        return new ExcelDataExtractor<CreateGymDTO>() {
            private final DataFormatter dataFormatter = new DataFormatter();

            @Override
            protected CreateGymDTO map(Row row) {

                int rowNum = row.getRowNum() + 1;

                String imagePathInfo = row.getCell(7).getStringCellValue().trim();
                List<String> imageList = getExcelImageList(imagePathInfo);

                // 이미지 URL의 확장자 유효성 검사
                validateImageExtensions(imageList, rowNum);
                CreateGymDTO dto = CreateGymDTO.builder()
                        .id((long) row.getCell(0).getNumericCellValue())
                        .gymName(row.getCell(1).getStringCellValue().trim())
                        .dailyPrice((long) row.getCell(2).getNumericCellValue())
                        .monthlyPrice((long) row.getCell(3).getNumericCellValue())
                        .address(row.getCell(4).getStringCellValue().trim())
                        .description(row.getCell(5).getStringCellValue().trim())
                        .localId((long) row.getCell(6).getNumericCellValue())
                        .uploadFileNames(imageList)
                        .info(row.getCell(8).getStringCellValue().trim())
                        .popular(row.getCell(9).getStringCellValue().trim())
                        .build();

                return dto;
            }
        };
    }
    /**
     * 이미지 URL 목록의 확장자 유효성 검사
     *
     * @param imageUrls 이미지 URL 목록
     * @param rowNum 현재 처리 중인 행 번호
     * @throws IllegalArgumentException 유효하지 않은 확장자가 발견된 경우
     */
    private static void validateImageExtensions(List<String> imageUrls, int rowNum) {
        List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif");

        for (String imageUrl : imageUrls) {
            try {
                String extension = getExtensionFromUrl(imageUrl);
                if (!allowedExtensions.contains(extension.toLowerCase())) {
                    throw new IllegalArgumentException(rowNum + "행에서 문제발생, 이미지 확장자는 jpg, jpeg, png, gif만 허용됩니다.");
                }
            } catch (Exception e) {
                throw new IllegalArgumentException(rowNum + "행에서 문제발생, " + e.getMessage());
            }
        }
    }

    /**
     * URL에서 확장자 추출
     *
     * @param imageUrl 이미지 URL
     * @return 확장자
     */
    private static String getExtensionFromUrl(String imageUrl) {
        try {
            // URL 파라미터에서 실제 이미지 URL 추출 시도
            if (imageUrl.contains("?src=")) {
                String encodedRealUrl = imageUrl.substring(imageUrl.indexOf("?src=") + 5);
                // URL 디코딩
                String realUrl = URLDecoder.decode(encodedRealUrl, StandardCharsets.UTF_8.name());
                // 실제 URL에서 확장자 추출
                return getExtensionFromSimpleUrl(realUrl);
            }

            // 일반적인 경우
            return getExtensionFromSimpleUrl(imageUrl);
        } catch (Exception e) {
            throw new IllegalArgumentException("이미지 URL에서 확장자를 추출할 수 없습니다: " + imageUrl);
        }
    }

    /**
     * 단순 URL에서 확장자 추출
     *
     * @param url URL
     * @return 확장자
     */
    private static String getExtensionFromSimpleUrl(String url) {
        int lastDotIndex = url.lastIndexOf('.');
        if (lastDotIndex > 0) {
            String extension = url.substring(lastDotIndex + 1).toLowerCase();
            // 확장자에 추가 경로나 파라미터가 포함된 경우 제거
            if (extension.contains("?") || extension.contains("/")) {
                extension = extension.split("[?/]")[0];
            }
            return extension;
        }
        throw new IllegalArgumentException("이미지 확장자를 찾을 수 없습니다: " + url);
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
