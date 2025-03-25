package com.example.gympt.domain.review.service;

import com.example.gympt.domain.booking.entity.Booking;
import com.example.gympt.domain.booking.repository.BookingRepository;
import com.example.gympt.domain.gym.entity.Gym;
import com.example.gympt.domain.gym.repository.GymRepository;
import com.example.gympt.domain.member.entity.Member;
import com.example.gympt.domain.member.repository.MemberRepository;
import com.example.gympt.domain.moderation.service.ModerationService;
import com.example.gympt.domain.review.dto.ReviewRequestDTO;
import com.example.gympt.domain.review.dto.ReviewResponseDTO;
import com.example.gympt.domain.review.entity.Review;
import com.example.gympt.domain.review.repository.ReviewRepository;
import com.example.gympt.domain.trainer.dto.TrainerResponseDTO;
import com.example.gympt.domain.trainer.entity.Trainers;
import com.example.gympt.domain.trainer.repository.TrainerRepository;
import com.example.gympt.exception.CustomDoesntExist;
import com.example.gympt.notification.service.NotificationService;
import com.example.gympt.util.s3.CustomFileUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.gympt.notification.enums.NotificationType.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final GymRepository gymRepository;
    private final CustomFileUtil customFileUtil;
    private final TrainerRepository trainerRepository;
    private final BookingRepository bookingRepository;
    private final ModerationService moderationService;
    private final NotificationService notificationService;
    private final ReviewSummaryService reviewSummaryService;

    @Transactional
    @Override
    public void createReview(String email, ReviewRequestDTO reviewRequestDTO) {
        Member member = getMember(email);
        Gym gym = getGym(reviewRequestDTO.getGymId());

        Trainers trainers = null;
        if (reviewRequestDTO.getTrainerId() != null) {
            trainers = getTrainer(reviewRequestDTO.getTrainerId());
        }

        Booking booking = bookingRepository.findById(reviewRequestDTO.getBookingId()).orElseThrow(() -> new EntityNotFoundException("예약 내역이 없습니다 " + reviewRequestDTO.getBookingId()));

        if (booking.getGym() == null || !booking.getGym().equals(gym) ||
                (trainers != null && booking.getTrainers() != null && !booking.getTrainers().equals(trainers)) ||
                booking.getMember() == null || !booking.getMember().equals(member)) {
            throw new CustomDoesntExist("예약 내역이 없어 리뷰 작성이 불가능 합니다");
        }

        if(booking.getBookingDate().isAfter(LocalDateTime.now())){
            throw new CustomDoesntExist("아직 예약일이 지나지 않아 리뷰를 작성할 수 없습니다.");
        }
        // 만약 리뷰 이미지가 있으면 s3에 업로드
        String reviewImage = null;
        if (reviewRequestDTO.getReviewImage() != null && !reviewRequestDTO.getReviewImage().isEmpty()) {
            reviewImage = customFileUtil.uploadS3File(reviewRequestDTO.getReviewImage());
        }
        Review newReview = Review.from(reviewRequestDTO, member, gym, reviewImage, trainers);
        checkReviewModeration(newReview);
        reviewRepository.save(newReview);

    }

    @Transactional(readOnly = true)
    @Override
    public List<ReviewResponseDTO> getMyReviewList(String email) {
        return reviewRepository.findReviewsByEmail(email).stream().map(ReviewResponseDTO::from).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<ReviewResponseDTO> getGymReviews(Long gymId) {
        return reviewRepository.findByGymId(gymId).stream().map(ReviewResponseDTO::from).toList();
    }

    @Override
    public Long deleteReview(String email, Long reviewId) {
        Member member = this.getMember(email);
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 review 입니다"));
        if (!review.getMember().getEmail().equals(member.getEmail())) {
            throw new RuntimeException("리뷰 작성자 본인만 삭제 가능합니다");
        }
        customFileUtil.deleteS3File(review.getReviewImage());
        reviewRepository.delete(review);
        return review.getId();
    }

    @Override
    public List<ReviewResponseDTO> getReviewListByTrainer(String email) {
        Trainers trainers = getTrainerEmail(email);
        return trainers.getReviews().stream().map(ReviewResponseDTO::from).toList();
    }

    @Override
    public void deleteByGym(Gym gym) {
        List<Review> reviews = reviewRepository.findByGymId(gym.getId());
        for (Review review : reviews) {
            customFileUtil.deleteS3File(review.getReviewImage());
            reviewRepository.delete(review);
        }
    }

    @Override
    public List<ReviewResponseDTO> getTrainerReviews(Long trainerId) {
        List<Review> trainerReviews = reviewRepository.findByTrainerId(trainerId);
        return trainerReviews.stream().map(ReviewResponseDTO::from).toList();
    }




    @Transactional
    public void checkReviewModeration(Review review) {
        String reviewText = review.getContent();
        List<String> reviewTexts = List.of(reviewText);

        log.info("리뷰 ID: {} 모더레이션 요청 시작", review.getId());

        moderationService.moderateReview(reviewTexts)
                .doOnSubscribe(s -> log.info("리뷰 ID: {} 모더레이션 API 호출 구독 시작", review.getId()))
                .doOnNext(result -> log.info("리뷰 ID: {} 모더레이션 결과 수신 완료", review.getId()))
                .subscribe(
                        result -> {
                            try {
                                log.info("리뷰 ID: {} 모더레이션 결과 처리 시작", review.getId());
                                handleModerationResult(result, review);
                            } catch (Exception e) {
                                log.error("리뷰 ID: {} 모더레이션 결과 처리 중 예외 발생: {}", review.getId(), e.getMessage(), e);
                            }
                        },
                        error -> {
                            log.error("리뷰 ID: {} 모더레이션 API 호출 실패: {}", review.getId(), error.getMessage());
                            notificationService.sendNotificationToMember(
                                    review.getMember(),
                                    "리뷰 검토 중 오류가 발생했습니다",
                                    "시스템 오류가 발생했습니다. 나중에 다시 시도해주세요.",
                                    PROCESSING_ERROR
                            );
                            error.printStackTrace();
                        },
                        () -> log.info("리뷰 ID: {} 모더레이션 프로세스 완료", review.getId())
                );
    }

    @Transactional
    public void handleModerationResult(Map<String, Object> result, Review review) {
        log.info("리뷰 ID: {} 모더레이션 결과 처리 함수 시작", review.getId());

        try {
            // 결과 데이터 검증
            if (result == null) {
                log.error("리뷰 ID: {} 모더레이션 결과가 null입니다", review.getId());
                return;
            }

            // ChatGPT API 응답에서 결과 추출
            if (!result.containsKey("results")) {
                log.error("리뷰 ID: {} 모더레이션 결과에 'results' 키가 없습니다: {}", review.getId(), result);
                return;
            }

            List<Map<String, Object>> results = (List<Map<String, Object>>) result.get("results");

            if (results == null || results.isEmpty()) {
                log.error("리뷰 ID: {} 모더레이션 결과 리스트가 비어있습니다", review.getId());
                return;
            }

            // ChatGPT API 응답으로 변환된 결과 가져오기
            Map<String, Object> gptResult = results.get(0);

            if (!gptResult.containsKey("flagged")) {
                log.error("리뷰 ID: {} 모더레이션 결과에 'flagged' 키가 없습니다: {}", review.getId(), gptResult);
                return;
            }

            boolean flagged = (boolean) gptResult.get("flagged");
            log.info("리뷰 ID: {} 모더레이션 결과 - flagged: {}", review.getId(), flagged);

            if (flagged) {
                try {
                    // 어떤 카테고리가 위반되었는지 확인
                    if (!gptResult.containsKey("categories")) {
                        log.warn("리뷰 ID: {} 위반으로 표시되었으나 categories 정보가 없습니다", review.getId());
                    }

                    Map<String, Boolean> categories = (Map<String, Boolean>) gptResult.getOrDefault("categories", Map.of());

                    // 위반 카테고리들을 로그로 기록
                    List<String> violatedCategories = categories.entrySet().stream()
                            .filter(Map.Entry::getValue)
                            .map(Map.Entry::getKey)
                            .toList();


                    String reason = (String) gptResult.getOrDefault("reason", "알 수 없는 이유");
                    log.info("리뷰 ID: {}가 다음 카테고리에서 위반 감지됨: {}, 이유: {}",
                            review.getId(), violatedCategories, reason);

                    // 리뷰 비활성화
                    review.changeActive(false);
                    log.info("리뷰 ID: {} 비활성화 처리 완료", review.getId());

                    reviewRepository.save(review);
                    log.info("리뷰 ID: {} 저장 완료", review.getId());

                    log.info("리뷰 ID: {} 알림 발송 시작 - 위반 검출", review.getId());
                    notificationService.sendNotificationToMember(
                            review.getMember(),
                            "리뷰에 부적절한 내용이 감지되었습니다",
                            "리뷰 정책 위반이 감지되었습니다",
                            REVIEW_REJECTED
                    );
                    log.info("리뷰 ID: {} 알림 발송 완료 - 위반 검출", review.getId());
                } catch (Exception e) {
                    log.error("리뷰 ID: {} 위반 처리 중 예외 발생: {}", review.getId(), e.getMessage(), e);
                }
            } else {
                try {
                    // 문제없는 리뷰는 active를 true로 설정
                    review.changeActive(true);
                    log.info("리뷰 ID: {} 활성화 처리 완료", review.getId());

                    reviewRepository.save(review);
                    log.info("리뷰 ID: {} 저장 완료", review.getId());

                    log.info("리뷰 ID: {} 알림 발송 시작 - 승인", review.getId());
                    notificationService.sendNotificationToMember(
                            review.getMember(),
                            "리뷰가 승인되었습니다",
                            "귀하의 리뷰에 감사드립니다",
                            REVIEW_APPROVED
                    );
                    log.info("리뷰 ID: {} 알림 발송 완료 - 승인", review.getId());

                    log.info("리뷰 ID: {} 리뷰 요약 업데이트 시작", review.getId());
                    reviewSummaryService.updateReviewSummary(review);
                    log.info("리뷰 ID: {} 리뷰 요약 업데이트 완료", review.getId());
                } catch (Exception e) {
                    log.error("리뷰 ID: {} 승인 처리 중 예외 발생: {}", review.getId(), e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            log.error("리뷰 ID: {} 모더레이션 결과 처리 중 예외 발생: {}", review.getId(), e.getMessage(), e);
        }
    }

    private Trainers getTrainer(Long trainerId) {
        return trainerRepository.findById(trainerId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 트레이너 이름입니다 " + trainerId));
    }

    private Trainers getTrainerEmail(String email) {
        return trainerRepository.findByTrainerEmail(email).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 트레이너 이름입니다 " + email));
    }


    private Gym getGym(Long gymId) {
        return gymRepository.findById(gymId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 헬스장 입니다 "));
    }

    private Member getMember(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다"));
    }

}
