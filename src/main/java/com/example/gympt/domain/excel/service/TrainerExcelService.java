package com.example.gympt.domain.excel.service;

import com.example.gympt.domain.excel.creator.MemberCreator;
import com.example.gympt.domain.trainer.dto.TrainerSaveRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerExcelService {
     private final MemberCreator memberCreator;
    public Long register(List<TrainerSaveRequestDTO> registrationDtoList) {

        log.info("register dtoList: {}", registrationDtoList);
        for (int i = 0; i < registrationDtoList.size(); i++) {
            try {
                memberCreator.createTrainers(registrationDtoList.get(i));
            } catch (IllegalArgumentException e) {
                // 1행 부터 시작이기 때문에 2를 더한다.
                throw new IllegalArgumentException(i + 2 + "행에서 문제발생, " + e.getMessage());
            } catch (Exception e) {
                throw new IllegalArgumentException(i + 2 + "행에서 Fail문제 발생, " + e.getMessage());
            }
        }

        return (long) registrationDtoList.size();
    }

}
