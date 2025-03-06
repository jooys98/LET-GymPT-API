package com.example.gympt.domain.booking.repository;

import com.example.gympt.domain.booking.entity.Booking;
import com.example.gympt.domain.gym.entity.Gym;
import com.example.gympt.domain.trainer.entity.Trainers;
import jakarta.annotation.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    //트레이너가 null 일때를 대비한 쿼리
    @Query("SELECT B FROM Booking B WHERE B.member.email=:email AND B.gym=:gym AND ((:trainers IS NULL) OR (B.trainers=:trainers))")
    Optional<Booking> findBooking(String email, @Nullable Trainers trainers, Gym gym);

    @Query("select b from Booking b where b.member.email =:email")
    List<Booking> findByEmail(@Param("email") String email);
}

