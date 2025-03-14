package com.example.gympt.notification.service;

import com.example.gympt.domain.chat.dto.ChatMessageDTO;
import com.example.gympt.domain.member.entity.Member;
import com.example.gympt.domain.member.repository.MemberRepository;
import com.example.gympt.domain.reverseAuction.repository.AuctionRequestRepository;
import com.example.gympt.domain.reverseAuction.repository.AuctionTrainerBidRepository;
import com.example.gympt.domain.trainer.entity.Trainers;
import com.example.gympt.domain.trainer.repository.TrainerRepository;
import com.example.gympt.notification.dto.NotificationResponseDTO;
import com.example.gympt.notification.entity.Notification;
import com.example.gympt.notification.enums.NotificationType;
import com.example.gympt.notification.repository.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final FCMService fcmService;
    private final MemberRepository memberRepository;
    private final AuctionTrainerBidRepository auctionTrainerBidRepository;

    /**
     * 개인 알림 전송 메서드
     */

    @Transactional
    public void sendNotificationToMember(Member member, String title, String body, NotificationType type) {
        log.info("sendCouponToMember notification: member email {}", member.getEmail());
        Notification notification = Notification.of(
                type,
                member,
                title,
                body,
                member.getFcmToken()
        );
        notificationRepository.save(notification);
        this.fcmNotificationSender(member, title, body, type);
    }

    /**
     * 여러 회원에게 FCM 알림 전송
     *
     * @param memberList 회원 목록
     */
    @Transactional
    public void sendBulkNotification(List<Member> memberList, String title, String body, NotificationType type) {
        log.info("sendBulkNotification 시작: {} 명의 회원에게 알림 전송", memberList.size());

        for (Member member : memberList) {
            // 각 회원에게 알림 저장 및 전송
            if (member.getFcmToken() != null && !member.getFcmToken().isBlank()) {
                // DB에 알림 저장
                Notification notification = Notification.of(
                        type,
                        member,
                        title,
                        body,
                        member.getFcmToken()
                );
                notificationRepository.save(notification);

                // FCM 알림 전송
                fcmService.sendNotification(
                        member.getEmail(),
                        member.getFcmToken(),
                        title,
                        body,
                        type
                );
                log.info("알림 전송 완료: {}", member.getEmail());
            }
        }

        log.info("sendBulkNotification 종료됨");
    }


    ;

    /**
     * 역경매 최종 낙찰 트레이너에게 알림 전송
     *
     * @param targetEmail
     */
    @Transactional
    public void sendFinalSelected(String targetEmail) {
        log.info("sendFinalSelected notification: target targetEmail {}", targetEmail);
        Member member = memberRepository.getWithRoles(targetEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 회원이 없습니다. targetEmail: " + targetEmail));
        String title = "최종 낙찰을 축하드립니다";
        String body = "회원님들의 건강을 위한 귀하의 활동을 응원합니다.";

        // DB에 알림 저장
        Notification notification = Notification.of(
                NotificationType.TRAINER_APPROVAL,
                member,
                title,
                body,
                member.getFcmToken()
        );
        notificationRepository.save(notification);
        this.fcmNotificationSender(member, title, body, NotificationType.TRAINER_APPROVAL);
    }

    /**
     * 해당 지역 새 역경매가 올라오면 트레이너들에게 알림 전송
     * 권한이 trainer 인 회원 리스트를 파라미터로 받아야 함
     */
    @Transactional
    public void sendOpenActionToTrainer(Long localId) {
        log.info("sendOpenActionToTrainer notification: localId {}", localId);
//해당 지역에서 활동하는 트레이너 회원들을 조회
        List<Member> trainersInLocal = memberRepository.findMemberTrainerInLocal(localId);

        String title = "새로운 역경매 신청이 들어왔습니다";
        String body = "지금 입찰 신청을 해보세요";
        sendBulkNotification(trainersInLocal, title, body, NotificationType.OPEN_AUCTION);
    }

    /**
     * 참여한 역경매가 종료되면 해당 역경매에 참여한 트레이너들에게 알림 전송
     *
     * @param auctionId
     */
    @Transactional
    public void endedAuction(Long auctionId) {
        log.info("endedAuction notification: auctionId  {}", auctionId);
        //해당 역경매에 참여했던 트레이너 회원들을 조회
        List<Member> auctionInTrainers = auctionTrainerBidRepository.findMemberTrainerInAuction(auctionId);

        String title = "역경매가 종료되었습니다";
        String body = "참여하신 선생님들 감사합니다!";
        sendBulkNotification(auctionInTrainers, title, body, NotificationType.ENDED_AUCTION);
    }


    /**
     * 트레이너 승인 알림 전송
     *
     * @param targetEmail 알림을 받을 트레이너 이메일
     */
    public void sendTrainerApproval(String targetEmail) {
        log.info("sendSellerApproval notification: target targetEmail {}", targetEmail);
        Member member = memberRepository.getWithRoles(targetEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 회원이 없습니다. targetEmail: " + targetEmail));
        String title = "트레이너 승인";
        String body = "회원님들의 건강을 위한 귀하의 활동을 응원합니다.";

        // DB에 알림 저장
        Notification notification = Notification.of(
                NotificationType.TRAINER_APPROVAL,
                member,
                title,
                body,
                member.getFcmToken()
        );
        notificationRepository.save(notification);
        this.fcmNotificationSender(member, title, body, NotificationType.TRAINER_APPROVAL);
    }


    /**
     * 새 메세지 알림 전송
     * 트레이너 , 회원 모두 포함
     *
     * @param targetEmail 알림을 받을 회원 이메일
     */

    public void sendChattingMessage(String targetEmail, ChatMessageDTO chatMessageDTO, Member member) {
        log.info("sendChattingMessage notification: target targetEmail {}", targetEmail);
        // 수신자
        Member target = memberRepository.getWithRoles(targetEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 회원이 없습니다. targetEmail: " + targetEmail));
        //발신자
        String title = member.getName() + "님 에게 새 메세지가 도착하였습니다!";
        String body = chatMessageDTO.getMessage();

        Notification notification = Notification.of(
                NotificationType.NEW_MESSAGE,
                target,
                title,
                body,
                target.getFcmToken()
        );
        notificationRepository.save(notification);
        this.fcmNotificationSender(target, title, body, NotificationType.NEW_MESSAGE);
    }


    /**
     * 회원에게 보내지는 역경매 가격 업데이트 알림
     *
     * @param targetEmail
     */

    public void updatePriceAuctionToMember(String targetEmail) {
        log.info("updatePriceAuctionToMember notification: target targetEmail {}", targetEmail);
        Member member = memberRepository.getWithRoles(targetEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 회원이 없습니다. targetEmail: " + targetEmail));
        String title = "역경매 금액이 변동되었습니다";
        String body = "지금 확인해보세요.";

        // DB에 알림 저장
        Notification notification = Notification.of(
                NotificationType.PRICE_UPDATE,
                member,
                title,
                body,
                member.getFcmToken()
        );
        notificationRepository.save(notification);
        this.fcmNotificationSender(member, title, body, NotificationType.PRICE_UPDATE);
    }


    /**
     * 예약일에 가는 알림
     *
     * @param targetEmail
     */

    public void bookingDayNotification(String targetEmail) {
        log.info("sendFinalSelected notification: target targetEmail {}", targetEmail);
        Member member = memberRepository.getWithRoles(targetEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 회원이 없습니다. targetEmail: " + targetEmail));
        String title = "오늘은 예약 당일입니다";
        String body = "늦지 않게 방문해주세요";

        // DB에 알림 저장
        Notification notification = Notification.of(
                NotificationType.BOOKING_DAY,
                member,
                title,
                body,
                member.getFcmToken()
        );
        notificationRepository.save(notification);
        this.fcmNotificationSender(member, title, body, NotificationType.BOOKING_DAY);
    }


    public void newTrainerAuctionNotification(String targetEmail) {
        log.info("sendFinalSelected notification: target targetEmail {}", targetEmail);
        Member member = memberRepository.getWithRoles(targetEmail)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 회원이 없습니다. targetEmail: " + targetEmail));
        String title = "새 트레이너가 입찰하였습니다";
        String body = "지금 제안을 확인해보세요";

        // DB에 알림 저장
        Notification notification = Notification.of(
                NotificationType.NEW_AUCTION_TRAINER,
                member,
                title,
                body,
                member.getFcmToken()
        );
        notificationRepository.save(notification);
        this.fcmNotificationSender(member, title, body, NotificationType.NEW_AUCTION_TRAINER);
    }


    /**
     * 알림 목록 조회
     *
     * @param targetEmail 알림을 받을 회원 이메일
     * @return 알림 목록
     */
    public List<NotificationResponseDTO> list(String targetEmail) {
        Member member = memberRepository.getWithRoles(targetEmail)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일을 가진 회원이 없습니다. targetEmail: " + targetEmail));

        return notificationRepository.findByEmail(member.getEmail()).stream()
                .map(NotificationResponseDTO::from).toList();
    }


    /**
     * FCM 알림 전송
     *
     * @param member 회원
     * @param title  제목
     * @param body   내용
     */
    private void fcmNotificationSender(Member member, String title, String body, NotificationType type) {
        // FCM 알림 전송
        if (member.getFcmToken() != null) {
            log.info("sendSellerApprovalNotification: FCM 알림 전송됨! member.getFcmToken(): {}", member.getFcmToken());
            fcmService.sendNotification(
                    member.getEmail(),
                    member.getFcmToken(),
                    title,
                    body,
                    type
            );
        }
        log.info("sendSellerApprovalNotification 서비스 end");
    }


}


