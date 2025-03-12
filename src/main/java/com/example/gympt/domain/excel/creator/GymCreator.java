package com.example.gympt.domain.excel.creator;

import com.example.gympt.domain.member.dto.CreateGymDTO;
import com.example.gympt.domain.member.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class GymCreator {
    private final AdminService adminService;

    public void create(CreateGymDTO CreateGymDTO) {

        adminService.createGym(CreateGymDTO);

    }

}
