package com.example.gympt.domain.gym.service;

import com.example.gympt.domain.gym.dto.GymResponseDTO;
import com.example.gympt.domain.gym.dto.GymSearchRequestDTO;
import com.example.gympt.domain.gym.entity.Gym;
import com.example.gympt.domain.gym.repository.GymRepository;
import com.example.gympt.dto.PageRequestDTO;
import com.example.gympt.dto.PageResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class GymServiceImpl implements GymService {

    private final GymRepository gymRepository;

    @Override
    public PageResponseDTO<GymResponseDTO> getGyms(GymSearchRequestDTO gymSearchRequestDTO,
                                                   PageRequestDTO pageRequestDTO) {

// PageRequest 에 있는 페이지 사이즈와 갯수로 페이지 객체 생성
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize());
//검샥조건과 페이지 객체를 파라미터로 보내어 해당 검색 결과 + PageRequest 에서 요구하는 페이지를 반환
        List<GymResponseDTO> gymList = gymRepository.findByGym(gymSearchRequestDTO, pageable)
                .stream().map(this::entityToDTO).collect(Collectors.toList());

        Long totalCount = gymRepository.countByGym(gymSearchRequestDTO);
        //총 검색 결과 수 (totalCount)
        return new PageResponseDTO<>(gymList, pageRequestDTO, totalCount);
//PageResponseDTO 클래스 값 -> list , pageRequestDTO ( 클라이언트 요청 페이지 ) , 총 검색 결과 수 (Long)

    }
}
