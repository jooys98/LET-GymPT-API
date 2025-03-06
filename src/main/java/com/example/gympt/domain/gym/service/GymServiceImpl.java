package com.example.gympt.domain.gym.service;

import com.example.gympt.domain.category.entity.Local;
import com.example.gympt.domain.category.repository.LocalRepository;
import com.example.gympt.domain.gym.dto.GymResponseDTO;
import com.example.gympt.domain.gym.dto.GymSearchRequestDTO;
import com.example.gympt.domain.gym.entity.Gym;
import com.example.gympt.domain.gym.entity.GymImage;
import com.example.gympt.domain.gym.repository.GymRepository;
import com.example.gympt.domain.likes.entity.LikesGym;
import com.example.gympt.domain.likes.repository.LikesGymRepository;
import com.example.gympt.domain.review.entity.Review;
import com.example.gympt.domain.trainer.entity.TrainerSaveImage;
import com.example.gympt.dto.PageRequestDTO;
import com.example.gympt.dto.PageResponseDTO;
import jakarta.persistence.EntityNotFoundException;
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
    private final LocalRepository localRepository;
    private final LikesGymRepository likesGymRepository;

    @Override
    public PageResponseDTO<GymResponseDTO> getGyms(GymSearchRequestDTO gymSearchRequestDTO,
                                                   PageRequestDTO pageRequestDTO, String email) {

// PageRequest 에 있는 페이지 사이즈와 갯수로 페이지 객체 생성
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize());
//검샥조건과 페이지 객체를 파라미터로 보내어 해당 검색 결과 + PageRequest 에서 요구하는 페이지를 반환
        List<GymResponseDTO> gymList = gymRepository.findByGym(gymSearchRequestDTO, pageable)
                .stream().map(gym -> this.entityToDTO(gym, likesGymRepository.likes(email, gym.getId()))).toList();

        Long totalCount = gymRepository.countByGym(gymSearchRequestDTO);
        //총 검색 결과 수 (totalCount)
        return new PageResponseDTO<>(gymList, pageRequestDTO, totalCount);
//PageResponseDTO 클래스 값 -> list , pageRequestDTO ( 클라이언트 요청 페이지 ) , 총 검색 결과 수 (Long)

    }

    @Override
    public GymResponseDTO getGymById(Long id, String email) {
        Gym gym = gymRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 헬스장 입니다"));
        return entityToDTO(gym, likesGymRepository.likes(email, gym.getId()));
    }

public GymResponseDTO entityToDTO(Gym gym, boolean likes) {
        Local local = localRepository.findById(gym.getLocal().getId()).orElseThrow(() -> new EntityNotFoundException("해당 지역이 없습니다"));

        List<String> imageNames = gym.getImageList().stream()
                .map(GymImage::getGymImageName)
                .toList();
        return GymResponseDTO.builder()
                .id(gym.getId())
                .localName(local.getLocalName())
                .gymName(gym.getGymName())
                .address(gym.getAddress())
                .description(gym.getDescription())
                .dailyPrice(gym.getDailyPrice())
                .monthlyPrice(gym.getMonthlyPrice())
                .likesCount(gym.getLikesCount())
                .popular(gym.getPopular())
                .imageNames(imageNames)
                .reviewAverage(gym.getReviewAverage())
                .reviewCount(gym.getReviewCount())
                .likes(likes)
                .trainers(gym.getTrainers())
                .build();
        //TODO: 추후에 추가 리뷰 리스트 , 트레이너 리스트
    }
}
