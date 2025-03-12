package com.example.gympt.domain.excel.service;

import com.example.gympt.domain.excel.creator.GymCreator;
import com.example.gympt.domain.gym.repository.GymRepository;
import com.example.gympt.domain.member.dto.CreateGymDTO;
import com.example.gympt.util.s3.CustomFileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor

public class GymExcelService {


    private final GymRepository gymRepository;
    private final CustomFileUtil fileUtil;
    private final GymCreator gymCreator;

    public Long register(List<CreateGymDTO> dtoList) {

        log.info("register dtoList: {}", dtoList);
        for (int i = 0; i < dtoList.size(); i++) {
            try {
                gymCreator.create(dtoList.get(i));
            } catch (IllegalArgumentException e) {
                // 1행 부터 시작이기 때문에 2를 더한다.
                throw new IllegalArgumentException(i + 2 + "행에서 문제발생, " + e.getMessage());
            } catch (Exception e) {
                throw new IllegalArgumentException(i + 2 + "행에서 Fail문제 발생, " + e.getMessage());
            }
        }

        return (long) dtoList.size();
    }


}