package com.example.gympt.domain.category.service;

import com.example.gympt.domain.category.dto.LocalDTO;
import com.example.gympt.domain.category.dto.LocalParentDTO;
import com.example.gympt.domain.category.dto.LocalResponseDTO;
import com.example.gympt.domain.category.entity.Local;
import com.example.gympt.domain.category.repository.LocalRepository;
import com.example.gympt.domain.gym.dto.GymResponseDTO;
import com.example.gympt.domain.gym.entity.Gym;
import com.example.gympt.domain.gym.entity.GymImage;
import com.example.gympt.domain.gym.repository.GymRepository;
import com.example.gympt.domain.gym.service.GymService;
import com.example.gympt.domain.gym.service.GymServiceImpl;
import com.example.gympt.domain.likes.repository.LikesGymRepository;
import jakarta.persistence.EntityNotFoundException;
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
    private final GymServiceImpl gymService;
    private final LikesGymRepository likesGymRepository;

    @Transactional(readOnly = true)
    @Override
    public List<GymResponseDTO> getLocalGymList(Long localId, String email) {
        Local local = getLocal(localId);
        List<Gym> gyms = localRepository.findGymByLocalId(local.getId());
        List<GymResponseDTO> localGymList = gyms.stream().map(gym -> gymService.entityToDTO(gym, likesGymRepository.likes(email, gym.getId()))).toList();
        return localGymList;
    }//local id 애 해당하는 헬스장 리스트 -> 풀어서 LocalResponseDTO 로 변환 -> 다시 리스트로 만들기 !

    @Transactional(readOnly = true)
    @Override
    public List<LocalDTO> getAll() {
        List<Local> locals = localRepository.findAllLocal();
        List<LocalDTO> localDTOS = locals.stream().map(LocalDTO::from).toList();
        return localDTOS;
    }

    @Transactional(readOnly = true)
    @Override
    public List<LocalDTO> getSubLocals(Long localId) {
        List<Local> locals = localRepository.findByLocalId(localId);
        return locals.stream().map(LocalDTO::from).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<LocalParentDTO> getLocals(Long localId) {
        List<Local> locals = localRepository.findByLocalId(localId);
        return locals.stream().map(LocalParentDTO::from).toList();
    }

    //지역 전체 보기 - admin 용
    @Transactional(readOnly = true)
    @Override
    public List<LocalDTO> localList() {
        return localRepository.findAll().stream()
                .map(LocalDTO::from)
                .toList();
    }

    private Local getLocal(Long localId) {
        return localRepository.findById(localId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 지역입니다"));
    }
}
