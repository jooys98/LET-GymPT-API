package com.example.gympt.domain.reverseAuction.service;

import com.example.gympt.domain.category.entity.Local;
import com.example.gympt.domain.category.repository.LocalRepository;
import com.example.gympt.domain.member.entity.Member;
import com.example.gympt.domain.member.enums.MemberRole;
import com.example.gympt.domain.member.repository.MemberRepository;
import com.example.gympt.domain.reverseAuction.dto.*;
import com.example.gympt.domain.reverseAuction.entity.AuctionRequest;
import com.example.gympt.domain.reverseAuction.entity.AuctionTrainerBid;
import com.example.gympt.domain.reverseAuction.entity.MatchedAuction;
import com.example.gympt.domain.reverseAuction.enums.AuctionStatus;
import com.example.gympt.domain.reverseAuction.enums.AuctionTrainerStatus;
import com.example.gympt.domain.reverseAuction.repository.AuctionRequestRepository;
import com.example.gympt.domain.reverseAuction.repository.AuctionTrainerBidRepository;
import com.example.gympt.domain.reverseAuction.repository.MatchedAuctionRepository;
import com.example.gympt.domain.trainer.dto.TrainerResponseDTO;
import com.example.gympt.domain.trainer.entity.Trainers;
import com.example.gympt.domain.trainer.repository.TrainerRepository;
import com.example.gympt.exception.CustomDoesntExist;
import com.example.gympt.exception.CustomNotAccessHandler;
import com.example.gympt.exception.NoDuplicationException;
import com.example.gympt.notification.service.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import static com.example.gympt.domain.reverseAuction.enums.AuctionStatus.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ReverseAuctionServiceImpl implements ReverseAuctionService {

    private final MemberRepository memberRepository;
    private final AuctionRequestRepository auctionRequestRepository;
    private final LocalRepository localRepository;
    private final AuctionTrainerBidRepository auctionTrainerBidRepository;
    private final MatchedAuctionRepository matchedAuctionRepository;
    private final NotificationService notificationService;
    private final TrainerRepository trainerRepository;


    //유저의 역경매 신청 로직
    @Transactional
    @Override
    public Long applyAuction(AuctionRequestDTO auctionRequestDTO) {
        Member member = getMember(auctionRequestDTO.getEmail());

        if (!member.getMemberRoleList().contains(MemberRole.USER)) {
            throw new IllegalArgumentException("역경매 신청은 일반 회원만 가능합니다 ");
        }

        Optional<AuctionRequest> existingAuction = auctionRequestRepository.findByEmail(member.getEmail());

        // 기존 역경매가 존재하는 경우
        if (existingAuction.isPresent()) {
            List<AuctionStatus> status = existingAuction.get().getStatus();

            // 진행 중인 상태인 경우 (OPEN 또는 IN_PROGRESS)
            if (status.contains(OPEN)) {
                throw new NoDuplicationException("역경매 중복 신청은 불가능합니다");
            } else if (status.contains(IN_PROGRESS)) {
                throw new NoDuplicationException("이미 진행중인 역경매 내역이 있습니다");
            }
        }
        // 신청내역이 없거나 COMPLETED 상태라면 새로운 신청 처리
        Local local = getLocal(auctionRequestDTO.getLocalId());
        //사용자는 자신의 사는 지역이 아니더라도 원하는 지역에서 수업 신청이 가능하므로 동네 조회 비교 로직은 생략

        AuctionRequest auctionRequest = AuctionRequest.builder()
                .member(member)
                .height(auctionRequestDTO.getHeight())
                .weight(auctionRequestDTO.getWeight())
                .title(auctionRequestDTO.getTitle())
                .age(auctionRequestDTO.getAge())
                .request(auctionRequestDTO.getRequest())
                .medicalConditions(auctionRequestDTO.getMedicalConditions())
                .local(local)
                .build();
        auctionRequest.addGender(auctionRequestDTO.getGender());
        auctionRequest.changeStatus(OPEN);

        auctionRequestRepository.save(auctionRequest);
        //해당 지역 트레이너 들에게 알림 전송
        notificationService.sendOpenActionToTrainer(local.getId());
        return auctionRequest.getId();
    }

    //트레이너 최종 낙찰 로직 + websocket
    @Transactional
    @Override
    public FinalSelectAuctionDTO selectTrainer(String email, Long trainerId) {
        Member member = getMember(email);
        Trainers trainers = getTrainer(trainerId);
        //진핸중인 사용자의 역경매 조회
        AuctionRequest auctionRequest = getAuctionRequestInProgress(email);

        if (!auctionRequest.getMember().equals(member)) {
            throw new CustomNotAccessHandler("경매 신청자 본인확인이 안되어 권한이 없습니다 ");
        }
        //역경매 상태 바꾸기
        auctionRequest.deleteStatus(IN_PROGRESS);
        auctionRequest.changeStatus(COMPLETED); // 최종 선택시 상태 변경
        auctionRequestRepository.save(auctionRequest);

        //역경매에 참여한 트레이너 리스트 불러오기
        List<AuctionTrainerBid> allBids = auctionTrainerBidRepository
                .findTrainersInAuction(auctionRequest.getId());

        // 각 입찰에 대해 선택/비선택 상태 설정
        for (AuctionTrainerBid bid : allBids) {
            if (bid.getTrainer().getMember().getEmail().equals(trainers.getMember().getEmail())) {
                bid.changeStatus(AuctionTrainerStatus.SELECTED);//선택된 트레이너 정보와 참여한 트레이너 정보가 일치하면 SELECTED
            } else {
                bid.changeStatus(AuctionTrainerStatus.UNSELECTED);
            }
            auctionTrainerBidRepository.saveAll(allBids);
        }
        //트레이너와 해당 역경매 row 가 같은 AuctionTrainerBid 가져오기
        AuctionTrainerBid auctionTrainerBid = auctionTrainerBidRepository.findByAuctionRequestIdAndTrainer(auctionRequest.getId(), trainers.getMember().getEmail())
                .orElseThrow(() -> new EntityNotFoundException("일치하는 정보가 없습니다"));

        //최종 매칭 테이블에 AuctionTrainerBid 참조값 저장하기
        MatchedAuction matchedAuction = MatchedAuction.builder()
                .auctionTrainerBid(auctionTrainerBid)
                .auctionRequest(auctionRequest)
                .finalPrice(auctionTrainerBid.getPrice()) // 트레이너가 제안한 최종 금액
                .build();

        matchedAuctionRepository.save(matchedAuction);
        //최종 매칭 테이블에 저장

//사용자에게 보낼 트레이너 정보들
        FinalSelectAuctionDTO finalSelectAuctionDTO = convertToSelectDTO(auctionRequest, matchedAuction);
        // 참여했던 트레이너에게 알림 전송
        notificationService.endedAuction(auctionRequest.getId());
        //낙찰 된 트레이너 에게 알림 전송
        notificationService.sendFinalSelected(matchedAuction.getAuctionTrainerBid().getTrainer().getMember().getEmail());
        return finalSelectAuctionDTO;
    }


    //TODO : 역경매 정보 회원 권한에 따라 분기처리 하는 방식으로 수정 하기

    @Transactional(readOnly = true)
    @Override
    public List<AuctionResponseDTO> getAuctionList() {
        List<AuctionRequest> auctionRequests = auctionRequestRepository.findAll();
        return auctionRequests.stream().map(this::AuctionEntityToDTO).toList();

    }

    //트레이너에게만 보여지는 정보
    @Transactional(readOnly = true)
    @Override
    public List<AuctionResponseToTrainerDTO> getAuctionListToTrainers() {
        List<AuctionResponseToTrainerDTO> auctionResponseDTOS = auctionRequestRepository.findAll().stream()
                .map(this::AuctionEntityForTrainersToDTO).toList();
        return auctionResponseDTOS;
    }

//    @Override
//    public AuctionTrainerNotificationDTO getSelectedMessage(String email) {
//        MatchedAuction matchedAuction = getAuction(email);
//        return convertToNotificationDTO(matchedAuction);
//    }

    //유저 , 트레이너 권한 분기처리  역경매 상세정보
    @Transactional(readOnly = true)
    @Override
    public Object getAuction(Long auctionRequestId, String email) {
        Member member = getMember(email);
        if (member.getMemberRoleList().contains(MemberRole.USER)) {
            AuctionResponseDTO auctionResponseDTO = auctionRequestRepository.findById(auctionRequestId)
                    .map(this::AuctionEntityToDTO).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 정보 입니다"));
            return auctionResponseDTO;
        } else {
            AuctionResponseToTrainerDTO auctionResponseToTrainerDTO = auctionRequestRepository.findById(auctionRequestId)
                    .map(this::AuctionEntityForTrainersToDTO).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 정보입니다"));
            return auctionResponseToTrainerDTO;
        }
    }

    //트레이너만 볼수 있는 역경매 상세정보
//    @Override
//    public AuctionResponseToTrainerDTO getAuctionToTrainer(Long auctionId) {
//        AuctionResponseToTrainerDTO auctionResponseToTrainerDTO = auctionRequestRepository.findById(auctionId)
//                .map(this::AuctionEntityForTrainersToDTO).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 정보입니다"));
//        return auctionResponseToTrainerDTO;
//    }
    @Transactional(readOnly = true)
    @Override
    public Member getMember(String email) {
        return memberRepository.getWithRoles(email).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다"));
    }

    @Transactional(readOnly = true)
    @Override
    public List<AuctionResponseDTO> getAuctionListInLocal(Long localId) {
        List<Local> LocalIds = localRepository.findByLocalId(localId);
        List<AuctionResponseDTO> auctionResponseDTOS = auctionRequestRepository.findByAuctionInLocal(LocalIds)
                .stream().map(auction -> this.AuctionEntityToDTO(auction)).toList();
        return auctionResponseDTOS;
    }

    @Transactional(readOnly = true)
    @Override
    public List<AuctionResponseToTrainerDTO> getAuctionListToTrainersInLocal(Long localId) {
        List<Local> LocalIds = localRepository.findByLocalId(localId);
        List<AuctionResponseToTrainerDTO> auctionResponseDTOS = auctionRequestRepository.findByAuctionInLocal(LocalIds)
                .stream().map(auction -> this.AuctionEntityForTrainersToDTO(auction)).toList();
        return auctionResponseDTOS;
    }

    @Override
    public Long cancelAuction(String email, Long auctionRequestId) {
        Member member = getMember(email);
        AuctionRequest auctionRequest = getAuctionRequestById(auctionRequestId);
        List<AuctionTrainerBid> auctionTrainer = getAuctionTrainerBid(email);
        if (!member.getEmail().equals(auctionRequest.getMember().getEmail())) {
            throw new CustomNotAccessHandler("삭제 권한이 없습니다");
        } else if (!auctionTrainer.isEmpty()) {
            throw new NoDuplicationException("참여하신 트레이너 분들이 계셔서 취소가 불가능 합니다");
        }
        auctionRequestRepository.delete(auctionRequest);

        return auctionRequest.getId();
    }

    @Override
    public List<AuctionTrainerBidResponseDTO> getTrainers(Long auctionRequestId) {
        AuctionRequest auctionRequest = getAuctionRequestById(auctionRequestId);
        List<AuctionTrainerBid> trainersInAuction = auctionTrainerBidRepository.findTrainersInAuction(auctionRequest.getId());
        return trainersInAuction.stream().map(this::convertToAuctionTrainerBidDTO).toList();

    }

    @Override
    public List<FinalSelectAuctionDTO> getAuctionHistory(String email) {
        // 1. 해당 이메일의 매칭된 경매 내역 가져오기
        List<MatchedAuction> matchedAuctions = matchedAuctionRepository.findByEmail(email);

        // 각 매칭된 경매에서 DTO 생성
        List<FinalSelectAuctionDTO> result = new ArrayList<>();

        for (MatchedAuction matchedAuction : matchedAuctions) {
            AuctionRequest auctionRequest = matchedAuction.getAuctionRequest(); //AuctionRequest 를 참조하는 역경매 결과 테이블에서 AuctionRequest 정보를 가져온다
            result.add(convertToSelectDTO(auctionRequest, matchedAuction));
        }
        return result;
    }

    @Override
    public List<AuctionTrainerHistoryDTO> getAuctionHistoryToTrainer(String email) {
        List<AuctionTrainerBid> auctionTrainerBidHistory = auctionTrainerBidRepository.findByTrainerEmail(email);
        return auctionTrainerBidHistory.stream().map(this::convertToAuctionTrainerHistory).toList();
    }


    //역경매 아이디로 조회
    private AuctionRequest getAuctionRequestById(Long auctionRequestId) {
        return auctionRequestRepository.findById(auctionRequestId).orElseThrow(() -> new EntityNotFoundException("신청 내역이 없습니다"));
    }

    //이메일로 역경매 조회
    private AuctionRequest getAuctionRequest(String email) {
        return auctionRequestRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("신청내역이 없습니다"));
    }

    //이메일에 해당하는 진행중인 역경매 진행
    private AuctionRequest getAuctionRequestInProgress(String email) {
        return auctionRequestRepository.findByProgressEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("신청 내역이 없습니다"));
    }


    //이메일에 해당하는 역경매에 참가한 트레이너 조회
    private List<AuctionTrainerBid> getAuctionTrainerBid(String email) {
        return auctionTrainerBidRepository.findByMemberEmail(email);
    }


    private Local getLocal(Long localId) {
        return localRepository.findLocalId(localId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 지역입니다" + localId));
    }


    private Trainers getTrainer(Long trainerId) {
        return trainerRepository.findById(trainerId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 트레이너 입니다 "));
    }

}
