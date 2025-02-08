package com.example.gympt.domain.trainer.service;

import com.example.gympt.domain.gym.entity.Gym;
import com.example.gympt.domain.gym.repository.GymRepository;
import com.example.gympt.domain.member.entity.Member;
import com.example.gympt.domain.member.enums.MemberRole;
import com.example.gympt.domain.member.repository.MemberRepository;
import com.example.gympt.domain.reverseAuction.dto.TrainerAuctionRequestDTO;
import com.example.gympt.domain.reverseAuction.entity.AuctionRequest;
import com.example.gympt.domain.reverseAuction.entity.AuctionTrainerBid;
import com.example.gympt.domain.reverseAuction.enums.AuctionStatus;
import com.example.gympt.domain.reverseAuction.repository.AuctionRequestRepository;
import com.example.gympt.domain.reverseAuction.repository.AuctionTrainerBidRepository;
import com.example.gympt.domain.trainer.dto.TrainerRequestDTO;
import com.example.gympt.domain.trainer.dto.TrainerResponseDTO;
import com.example.gympt.domain.trainer.dto.TrainerSaveRequestDTO;
import com.example.gympt.domain.trainer.entity.TrainerSaveForm;
import com.example.gympt.domain.trainer.entity.TrainerSaveImage;
import com.example.gympt.domain.trainer.entity.Trainers;
import com.example.gympt.domain.trainer.repository.TrainerRepository;
import com.example.gympt.domain.trainer.repository.TrainerSaveFormRepository;
import com.example.gympt.dto.PageRequestDTO;
import com.example.gympt.dto.PageResponseDTO;
import com.example.gympt.util.s3.CustomFileUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.gympt.domain.member.enums.MemberRole.PREPARATION_TRAINER;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TrainerServiceImpl implements TrainerService {


    private final MemberRepository memberRepository;
    private final GymRepository gymRepository;
    private final TrainerSaveFormRepository trainerSaveFormRepository;
    private final TrainerRepository trainerRepository;
    private final CustomFileUtil customFileUtil;
    private final AuctionRequestRepository auctionRequestRepository;
    private final AuctionTrainerBidRepository auctionTrainerBidRepository;


    @Override
//트레이너 데뷔 신청!!
    public void saveTrainer(String trainerEmail, TrainerSaveRequestDTO trainerSaveRequestDTO) {
        Member member = getMemberRoles(trainerEmail);

        if (!member.getMemberRoleList().contains(MemberRole.PREPARATION_TRAINER)) {
            throw new RuntimeException("트레이너 신청 회원이 아닙니다");
        }
        Gym gym = getGym(trainerSaveRequestDTO.getGymId());

        List<String> images = customFileUtil.uploadS3Files(trainerSaveRequestDTO.getFiles());
        //trainerSaveRequestDTO 이미지 이름 문자열 리스트 가져와서 s3 업로드 후 이미지 이름 문자열 반환
        List<TrainerSaveImage> trainerSaveImages = images.stream().map(TrainerSaveForm::addImageString).toList();
        //이미지 객체 변환
        TrainerSaveForm trainerSaveForm = TrainerSaveForm.builder()
                .member(member)
                .gym(gym)
                .name(trainerSaveRequestDTO.getName())
                .age(trainerSaveRequestDTO.getAge())
                .gender(trainerSaveRequestDTO.getGender())
                .introduction(trainerSaveRequestDTO.getIntroduction())
                .imageList(trainerSaveImages)
                .build();
        trainerSaveFormRepository.save(trainerSaveForm);
    }


    //트레이너 조건 별 정보 조회!
    @Override
    public PageResponseDTO<TrainerResponseDTO> getTrainers(TrainerRequestDTO trainerRequestDTO, PageRequestDTO pageRequestDTO) {
        Pageable pageable = PageRequest.of(pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize());
        List<TrainerResponseDTO> trainerList = trainerRepository.findTrainers(trainerRequestDTO, pageable)
                .stream().map(this::trainerEntityToDTO).collect(Collectors.toList());
        Long totalCount = trainerRepository.countTrainers(trainerRequestDTO);
        return new PageResponseDTO<>(trainerList, pageRequestDTO, totalCount);
    }

    //트레이너 상세 조회
    @Override
    public TrainerResponseDTO getTrainerById(Long id) {
        Trainers trainers = trainerRepository.findById(id).orElseThrow(() -> new RuntimeException("존재하지 않는 트레이너 입니다"));
        return trainerEntityToDTO(trainers);
    }

    //트레이너 입찰 신청
    @Override
    public void applyAuction(String trainerEmail, TrainerAuctionRequestDTO trainerAuctionRequestDTO) {

        Trainers trainers = getTrainers(trainerEmail);
        if (!trainers.getLocal().getLocalName().equals(trainerAuctionRequestDTO.getLocalName())) {
            throw new IllegalArgumentException("활동하시는 동네에서만 입찰 신창이 가능합니다 ");
        }
        AuctionRequest auctionRequest = auctionRequestRepository.findById(trainerAuctionRequestDTO.getAuctionRequestId())
                .orElseThrow(() -> new EntityNotFoundException("해당 역경매는 존재하지 않습니다"));

        auctionRequest.setStatus(Collections.singletonList(AuctionStatus.IN_PROGRESS));
        //트레이너 입찰시 open 에서 진행중으로 상태 변경

        AuctionTrainerBid auctionTrainerBid = AuctionTrainerBid.builder()
                .auctionRequest(auctionRequest)
                .price(trainerAuctionRequestDTO.getPrice())
                .proposalContent(trainerAuctionRequestDTO.getProposalContent())
                .trainer(trainers)
                .trainerImage(String.valueOf(trainers.getImageList().get(0))) //이미지 한장만 가져오기
                .build();
        auctionTrainerBidRepository.save(auctionTrainerBid);

    }

//TODO : 사용자 확인용, 트레이너 확인용  입찰한 트레이너 리스트 보기 로직
    //TODO: 사용자/트레이너  역경매 취소 로직

    //트레이너 pt 가격 변경
    @Override
    public void changePrice(Long auctionRequestId, String trainerEmail, Long updatePrice) {

        Trainers trainers = getTrainers(trainerEmail);
        AuctionTrainerBid auctionTrainerBid = auctionTrainerBidRepository
                .findByAuctionRequestIdAndTrainer(auctionRequestId, trainers.getMember().getEmail())
                .orElseThrow(() -> new RuntimeException("입찰 내역이 없습니다"));
        //가격변경 신청한 본인이 입찰한 트레이너가 맞는지 다시 한번 확인

        auctionTrainerBid.changePrice(updatePrice);
        auctionTrainerBidRepository.save(auctionTrainerBid);
    }




    private Member getMemberRoles(String email) {
        return memberRepository.getWithRoles(email)
                .orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다"));
    }

    private Gym getGym(Long gymId) {
        return gymRepository.findByGymId(gymId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 헬스장 입니다 "));
    }

    private Trainers getTrainers(String trainerEmail) {
        return trainerRepository.findByTrainerEmail(trainerEmail)
                .orElseThrow(() -> new EntityNotFoundException("트레이너 권한이 없습니다"));
    }

}
