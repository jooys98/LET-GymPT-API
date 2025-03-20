package com.example.gympt.domain.booking.service;

import com.example.gympt.domain.booking.dto.BookingRequestDTO;
import com.example.gympt.domain.booking.dto.BookingResponseDTO;
import com.example.gympt.domain.booking.dto.BookingUpdateDTO;
import com.example.gympt.domain.booking.entity.Booking;
import com.example.gympt.domain.booking.repository.BookingRepository;
import com.example.gympt.domain.gym.entity.Gym;
import com.example.gympt.domain.gym.repository.GymRepository;
import com.example.gympt.domain.member.entity.Member;
import com.example.gympt.domain.member.repository.MemberRepository;
import com.example.gympt.domain.trainer.entity.Trainers;
import com.example.gympt.domain.trainer.repository.TrainerRepository;
import com.example.gympt.exception.CustomDoesntExist;
import com.example.gympt.exception.CustomNotAccessHandler;
import com.example.gympt.exception.NoDuplicationException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final MemberRepository memberRepository;
    private final TrainerRepository trainerRepository;
    private final GymRepository gymRepository;

    @Override
    public List<BookingResponseDTO> getBookingList(String email) {
        Member member = getMember(email);
        List<Booking> bookings = bookingRepository.findByEmail(member.getEmail());
        return bookings.stream().map(BookingResponseDTO::from).toList();
    }


    @Override
    public Long bookingGym(String email, BookingRequestDTO bookingRequestDTO) {
        Member member = getMember(email);
        Gym gym = gymRepository.findById(bookingRequestDTO.getGymId()).orElseThrow(() -> new EntityNotFoundException("헬스장 예약란은 필수입니다"));

        if (bookingRequestDTO.getBookingDate().isBefore(LocalDateTime.now())) {
            throw new NoDuplicationException("현재날짜보다 이전의 날짜는 예약이 불가능 합니다.");
        }

        Trainers trainers = null;
        if (bookingRequestDTO.getTrainerId() != null) {
            trainers = trainerRepository.findById(bookingRequestDTO.getTrainerId()).orElse(null);
        }

        Booking Newbooking = Booking.from(member, gym, trainers, bookingRequestDTO.getBookingDate());
        bookingRepository.save(Newbooking);
        return Newbooking.getId();
    }


    @Override
    public Long deleteBooking(String email, Long id) {
        Member member = getMember(email);
        Booking booking = getBooking(id);
        if (!member.getEmail().equals(booking.getMember().getEmail())) {
            throw new CustomNotAccessHandler("삭제 권한이 없습니다");
        }
        bookingRepository.delete(booking);
        trainerRepository.delete(booking.getTrainers());
        gymRepository.delete(booking.getGym());
        return booking.getId();
    }

    @Override
    public Long modifyBooking(String email, Long id, BookingUpdateDTO bookingUpdateDTO) {
        Member member = getMember(email);
        Booking booking = getBooking(id);
        if (!member.getEmail().equals(booking.getMember().getEmail())) {
            throw new CustomDoesntExist("날짜 변경 권한이 없습니다");
        }
        booking.changeBookingDate(bookingUpdateDTO.getBookingDate());
        return bookingRepository.save(booking).getId();
    }

    @Override
    public List<BookingResponseDTO> getBookingListByTrainer(String email) {
        Trainers trainers = getTrainers(email);
        return trainers.getBooking().stream().map(BookingResponseDTO::from).toList();

    }

    private Booking getBooking(Long id) {
        return bookingRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("해당 예약 내역이 존재하지 않습니다"));
    }

    private Member getMember(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("Member not found"));
    }

    private Trainers getTrainers(String email) {
        return trainerRepository.findByTrainerEmail(email).orElseThrow(() -> new EntityNotFoundException("Trainers not found"));
    }
}
