package com.example.gympt.domain.booking.service;

import com.example.gympt.domain.booking.dto.BookingRequestDTO;
import com.example.gympt.domain.booking.dto.BookingResponseDTO;
import com.example.gympt.domain.booking.dto.BookingUpdateDTO;
import com.example.gympt.domain.booking.entity.Booking;
import com.example.gympt.domain.gym.entity.Gym;
import com.example.gympt.domain.member.entity.Member;
import com.example.gympt.domain.trainer.entity.Trainers;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {
    List<BookingResponseDTO> getBookingList(String email);

    Long bookingGym(String email, BookingRequestDTO bookingRequestDTO);

    Long deleteBooking(String email, Long id);

    Long modifyBooking(String email, Long id, BookingUpdateDTO bookingUpdateDTO);

    List<BookingResponseDTO> getBookingListByTrainer(String email);

    default BookingResponseDTO convertToDTO(Booking booking) {
        return BookingResponseDTO.builder()
                .id(booking.getId())
                .gymId(booking.getGym().getId())
                .gymName(booking.getGym().getGymName())
                .gymAddress(booking.getGym().getAddress())
                .trainerName(booking.getTrainers()==null ? null : booking.getTrainers().getTrainerName())
                .trainerId(booking.getTrainers()==null ? null : booking.getTrainers().getId())
                .bookingDate(booking.getBookingDate())
                .build();

    }

    default Booking convertToEntity(Member member, Gym gym, Trainers trainers, LocalDateTime bookingDate) {
        return Booking.builder()
                .gym(gym)
                .trainers(trainers == null ? null : trainers)
                .member(member)
                .bookingDate(bookingDate)
                .build();
    }


}
