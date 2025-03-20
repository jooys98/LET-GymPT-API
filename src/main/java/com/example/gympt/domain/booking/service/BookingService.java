package com.example.gympt.domain.booking.service;

import com.example.gympt.domain.booking.dto.BookingRequestDTO;
import com.example.gympt.domain.booking.dto.BookingResponseDTO;
import com.example.gympt.domain.booking.dto.BookingUpdateDTO;
import com.example.gympt.domain.booking.entity.Booking;
import com.example.gympt.domain.gym.entity.Gym;
import com.example.gympt.domain.gym.entity.GymImage;
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






}
