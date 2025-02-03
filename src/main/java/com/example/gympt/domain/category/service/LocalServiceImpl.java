package com.example.gympt.domain.category.service;

import com.example.gympt.domain.category.dto.LocalDTO;
import com.example.gympt.domain.category.dto.LocalResponseDTO;
import com.example.gympt.domain.category.entity.Local;
import com.example.gympt.domain.category.repository.LocalRepository;
import com.example.gympt.domain.gym.entity.Gym;
import com.example.gympt.domain.gym.entity.GymImage;
import com.example.gympt.domain.gym.repository.GymRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor

public class LocalServiceImpl implements LocalService {


    private final LocalRepository localRepository;
    private final GymRepository gymRepository;


    @Override
    public List<LocalResponseDTO> getLocalGymList(Long localId) {
        Local local = localRepository.findById(localId).orElseThrow(() -> new RuntimeException("존재하지 않는 지역입니다"));
        List<Gym> gyms = gymRepository.findByLocalId(local.getId());
        List<LocalResponseDTO> localResponseDTOS = gyms.stream().map(this::convertToLocalDTO).collect(Collectors.toList());
        return localResponseDTOS;
    }//local id 애 해당하는 헬스장 리스트 -> 풀어서 LocalResponseDTO 로 변환 -> 다시 리스트로 만들기 !

    private LocalResponseDTO convertToLocalDTO(Gym gym) {
        //gym -> LocalResponseDTO
        List<String> imageNames = gym.getImageList().stream().map(GymImage::getGymImageName).toList();
        return LocalResponseDTO.builder()
                .id(gym.getLocal().getId())
                .localName(gym.getLocal().getLocalName())
                .gymName(gym.getGymName())
                .address(gym.getAddress())
                .dailyPrice(gym.getDailyPrice())
                .monthlyPrice(gym.getMonthlyPrice())
                .likesCount(gym.getLikesCount())
                .popular(gym.getPopular())
                .uploadFileNames(imageNames)
                .build();
    }

    @Override
    public List<LocalDTO> getAll() {
        List<Local> locals = localRepository.findAll();
        List<LocalDTO> localDTOS = locals.stream().map(this::convertToDTO).collect(Collectors.toList());
        return localDTOS;
    }

    private LocalDTO convertToDTO(Local local) {
        return LocalDTO.builder()
                .id(local.getId())
                .localName(local.getLocalName())
                .build();
    }

}
