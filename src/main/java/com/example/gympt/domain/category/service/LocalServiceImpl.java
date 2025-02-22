package com.example.gympt.domain.category.service;

import com.example.gympt.domain.category.dto.LocalDTO;
import com.example.gympt.domain.category.dto.LocalParentDTO;
import com.example.gympt.domain.category.dto.LocalResponseDTO;
import com.example.gympt.domain.category.entity.Local;
import com.example.gympt.domain.category.repository.LocalRepository;
import com.example.gympt.domain.gym.entity.Gym;
import com.example.gympt.domain.gym.entity.GymImage;
import com.example.gympt.domain.gym.repository.GymRepository;
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


    @Override
    public List<LocalResponseDTO> getLocalGymList(Long localId) {
        Local local = getLocal(localId);
        List<Gym> gyms = localRepository.findGymByLocalId(local.getId());
        List<LocalResponseDTO> localResponseDTOS = gyms.stream().map(this::convertToLocalDTO).toList();
        return localResponseDTOS;
    }//local id 애 해당하는 헬스장 리스트 -> 풀어서 LocalResponseDTO 로 변환 -> 다시 리스트로 만들기 !


    @Override
    public List<LocalDTO> getAll() {
        List<Local> locals = localRepository.findAllLocal();
        List<LocalDTO> localDTOS = locals.stream().map(this::convertToDTO).collect(Collectors.toList());
        return localDTOS;
    }

    @Override
    public List<LocalDTO> getSubLocals(Long localId) {
        List<Local> locals = localRepository.findByLocalId(localId);
        return locals.stream().map(this::convertToDTO).toList();
    }

    @Override
    public List<LocalParentDTO> getLocals(Long localId) {
        List<Local> locals = localRepository.findByLocalId(localId);
        return locals.stream().map(this::convertToLocalParentDTO).toList();
    }


    private Local getLocal(Long localId) {
        return localRepository.findById(localId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 지역입니다"));
    }
}
