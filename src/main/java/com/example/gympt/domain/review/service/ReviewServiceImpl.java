package com.example.gympt.domain.review.service;

import com.example.gympt.domain.booking.entity.Booking;
import com.example.gympt.domain.booking.repository.BookingRepository;
import com.example.gympt.domain.gym.entity.Gym;
import com.example.gympt.domain.gym.repository.GymRepository;
import com.example.gympt.domain.member.entity.Member;
import com.example.gympt.domain.member.repository.MemberRepository;
import com.example.gympt.domain.review.dto.ReviewRequestDTO;
import com.example.gympt.domain.review.dto.ReviewResponseDTO;
import com.example.gympt.domain.review.entity.Review;
import com.example.gympt.domain.review.repository.ReviewRepository;
import com.example.gympt.domain.trainer.entity.Trainers;
import com.example.gympt.domain.trainer.repository.TrainerRepository;
import com.example.gympt.exception.CustomDoesntExist;
import com.example.gympt.util.s3.CustomFileUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;

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

    @Override
    public void createReview(String username, ReviewRequestDTO reviewRequestDTO) {
        Member member = this.getMember(username);
        Gym gym = getGym(reviewRequestDTO.getGymId());
        Trainers trainers = getTrainer(reviewRequestDTO.getTrainerName());

        Booking booking = bookingRepository.findBooking(member.getEmail(), trainers, gym).orElseThrow(() -> new EntityNotFoundException("Booking not found"));
        if (!booking.getGym().equals(gym) && !booking.getTrainers().equals(trainers) && booking.getMember().equals(member)) {
            throw new CustomDoesntExist("예약 내역이 없어 리뷰 작성이 불가능 합니다");
        }
        // 만약 리뷰 이미지가 있으면 s3에 업로드
        String reviewImage = null;
        if (reviewRequestDTO.getReviewImage() != null && !reviewRequestDTO.getReviewImage().isEmpty()) {
            reviewImage = customFileUtil.uploadS3File(reviewRequestDTO.getReviewImage());
        }
        Trainers trainerName = null;
        if (reviewRequestDTO.getTrainerName() != null && !reviewRequestDTO.getTrainerName().isEmpty()) {
            trainerName = getTrainer(reviewRequestDTO.getTrainerName());
        }
        Review newReview = dtoToEntity(reviewRequestDTO, member, gym, reviewImage, trainerName);
        reviewRepository.save(newReview);

    }

    @Transactional(readOnly = true)
    @Override
    public List<ReviewResponseDTO> getMyReviewList(String username) {
        return reviewRepository.findReviewsByEmail(username).stream().map(this::EntityToDTO).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<ReviewResponseDTO> getGymReviews(Long gymId) {
        return reviewRepository.findByGymId(gymId).stream().map(this::EntityToDTO).toList();
    }

    @Override
    public Long deleteReview(String username, Long reviewId) {
        Member member = this.getMember(username);
        Review review = reviewRepository.findById(reviewId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 review 입니다"));
        if (!review.getMember().getEmail().equals(member.getEmail())) {
            throw new RuntimeException("리뷰 작성자 본인만 삭제 가능합니다");
        }
        customFileUtil.deleteS3File(review.getReviewImage());
        reviewRepository.delete(review);
        return review.getId();
    }

    @Override
    public void deleteByGym(Gym gym) {
        List<Review> reviews = reviewRepository.findByGymId(gym.getId());
        for (Review review : reviews) {
            customFileUtil.deleteS3File(review.getReviewImage());
            reviewRepository.delete(review);
        }
    }


    private Trainers getTrainer(String trainerName) {
        return trainerRepository.findByTrainerEmail(trainerName).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 트레이너 이름입니다 " + trainerName));
    }


    private Gym getGym(Long gymId) {
        return gymRepository.findByGymId(gymId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 헬스장 입니다 "));
    }

    private Member getMember(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다"));
    }

}
