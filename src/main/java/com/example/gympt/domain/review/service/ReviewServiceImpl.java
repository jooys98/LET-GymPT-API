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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.gympt.notification.enums.NotificationType.REVIEW_APPROVED;
import static com.example.gympt.notification.enums.NotificationType.REVIEW_REJECTED;

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
        // 만약 리뷰 이미지가 있으면 s3에 업로드
        String reviewImage = null;
        if (reviewRequestDTO.getReviewImage() != null && !reviewRequestDTO.getReviewImage().isEmpty()) {
            reviewImage = customFileUtil.uploadS3File(reviewRequestDTO.getReviewImage());
        }

        Review newReview = dtoToEntity(reviewRequestDTO, member, gym, reviewImage, trainers);
        checkReviewModeration(newReview);
        reviewRepository.save(newReview);

    }

    @Transactional(readOnly = true)
    @Override
    public List<ReviewResponseDTO> getMyReviewList(String email) {
        return reviewRepository.findReviewsByEmail(email).stream().map(this::EntityToDTO).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<ReviewResponseDTO> getGymReviews(Long gymId) {
        return reviewRepository.findByGymId(gymId).stream().map(this::EntityToDTO).toList();
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
        return trainers.getReviews().stream().map(this::EntityToDTO).toList();
    }

    @Override
    public void deleteByGym(Gym gym) {
        List<Review> reviews = reviewRepository.findByGymId(gym.getId());
        for (Review review : reviews) {
            customFileUtil.deleteS3File(review.getReviewImage());
            reviewRepository.delete(review);
        }
    }

    //비동기 호출
    @Transactional
    public void checkReviewModeration(Review review) {
        String reviewText = review.getContent();

        List<String> reviewTexts = List.of(reviewText);

        moderationService.moderateReview(reviewTexts)
                .subscribe(     //result : 떡볶이를 담을 접시
                                //review : 신전 떡볶이
                        // subscribe 안에서 실행되는 동작은 전부 비동기작용이고
                        // 별도의 스레드에서 실행되기 때문에 트랜젝션 처리와 관련이 없다
                        result -> handleModerationResult(result, review),
                        error -> {
                            log.error("모더레이션 API 오류: {}", error.getMessage());
                            error.printStackTrace();
                        }

                );
    }

    //리뷰 검사 결과에 따라 별도의 트랜잭션으로 리뷰 상태가 업데이트 , 배달 완료된 신전 떡볶이
    @Transactional
    public void handleModerationResult(Map<String, Object> result, Review review) {
        try {
            // ChatGPT API 응답에서 결과 추출
            List<Map<String, Object>> results = (List<Map<String, Object>>) result.get("results");

            if (results != null && !results.isEmpty()) {
                // ChatGPT API 응답으로 변환된 결과 가져오기
                Map<String, Object> gptResult = results.get(0);
                boolean flagged = (boolean) gptResult.get("flagged");

                if (flagged) {
                    // 어떤 카테고리가 위반되었는지 확인
                    Map<String, Boolean> categories = (Map<String, Boolean>) gptResult.get("categories");

                    // 위반 카테고리들을 로그로 기록
                    List<String> violatedCategories = categories.entrySet().stream()
                            .filter(Map.Entry::getValue)
                            .map(Map.Entry::getKey)
                            .collect(Collectors.toList());

                    // 위반 이유 로깅 (reason 필드가 있는 경우)
                    String reason = (String) gptResult.getOrDefault("reason", "알 수 없는 이유");
                    log.info("리뷰 ID: {}가 다음 카테고리에서 위반 감지됨: {}, 이유: {}",
                            review.getId(), violatedCategories, reason);

                    // 리뷰 비활성화
                    review.changeActive(false);
                    reviewRepository.save(review);
                    notificationService.sendNotificationToMember(
                            review.getMember(),
                            "리뷰에 부적절한 내용이 감지되었습니다",
                            "리뷰 정책 위반이 감지되었습니다",
                            REVIEW_REJECTED
                    );
                } else {
                    // 문제없는 리뷰는 active를 true로 설정
                    review.changeActive(true);
                    reviewRepository.save(review);
                    notificationService.sendNotificationToMember(
                            review.getMember(),
                            "리뷰가 승인되었습니다",
                            "귀하의 리뷰에 감사드립니다",
                            REVIEW_APPROVED
                    );
                    reviewSummaryService.updateReviewSummary(review);
                }
            }
        } catch (Exception e) {
            log.error("모더레이션 결과 처리 중 오류: {}", e.getMessage(), e);

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
