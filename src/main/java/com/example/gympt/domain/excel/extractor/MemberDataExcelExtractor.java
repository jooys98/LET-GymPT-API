package com.example.gympt.domain.excel.extractor;

import com.example.gympt.domain.excel.util.ExcelDataExtractor;
import com.example.gympt.domain.member.dto.JoinRequestDTO;
import com.example.gympt.domain.trainer.dto.TrainerSaveRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class MemberDataExcelExtractor {

    public static List<JoinRequestDTO> extract(MultipartFile file) {
        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(file.getBytes()))) {
            ExcelDataExtractor<JoinRequestDTO> extractor = getExtractor();
            return extractor.extract(workbook.getSheetAt(0));
        } catch (IOException e) {
            throw new RuntimeException("엑셀 파일을 읽는 중 오류가 발생했습니다.", e);
        }
    }

    private static ExcelDataExtractor<JoinRequestDTO> getExtractor() {
        return new ExcelDataExtractor<JoinRequestDTO>() {
            private final DataFormatter dataFormatter = new DataFormatter();

            @Override
            protected JoinRequestDTO map(Row row) {
                JoinRequestDTO dto = JoinRequestDTO.builder()
                        .email(row.getCell(0).getStringCellValue().trim())
                        .name(row.getCell(1).getStringCellValue().trim())
                        .password(row.getCell(2).getStringCellValue().trim())
                        .phone(row.getCell(3).getStringCellValue().trim())
                        .address(row.getCell(4).getStringCellValue().trim())
                        .localName(row.getCell(5).getStringCellValue().trim())
                        .gender(row.getCell(6).getStringCellValue().trim())
                        .birthday(dataFormatter.formatCellValue(row.getCell(7)).trim())
                        .role(row.getCell(8).getStringCellValue().trim())
                        .build();

                return dto;
            }
        };
    }

}
