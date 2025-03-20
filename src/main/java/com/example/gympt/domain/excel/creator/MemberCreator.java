package com.example.gympt.domain.excel.creator;

import com.example.gympt.domain.member.dto.JoinRequestDTO;
import com.example.gympt.domain.member.service.AdminService;
import com.example.gympt.domain.member.service.MemberService;
import com.example.gympt.domain.trainer.dto.TrainerSaveRequestDTO;
import com.example.gympt.domain.trainer.service.TrainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class MemberCreator {

    private final AdminService adminService;
    private final TrainerService trainerService;
    private final MemberService memberService;

    public void createTrainers(TrainerSaveRequestDTO trainerSaveRequestDTO) {
        trainerService.saveTrainer(trainerSaveRequestDTO.getEmail(), trainerSaveRequestDTO);

    }

    public void createMembers(JoinRequestDTO requestDTO) {
        memberService.join(requestDTO);
    }
}
