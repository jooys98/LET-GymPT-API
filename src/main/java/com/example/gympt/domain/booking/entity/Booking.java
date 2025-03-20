package com.example.gympt.domain.booking.entity;

import com.example.gympt.domain.gym.entity.Gym;
import com.example.gympt.domain.member.entity.Member;
import com.example.gympt.domain.trainer.entity.Trainers;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "booking_tbl")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email")
    private Member member;

    // 예약 날짜
    private LocalDateTime bookingDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gym_id")
    private Gym gym;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id" , nullable = true)
    private Trainers trainers;

public void changeBookingDate(LocalDateTime bookingDate) {
    this.bookingDate = bookingDate;
}

    public static Booking from(Member member, Gym gym, Trainers trainers, LocalDateTime bookingDate) {
        return Booking.builder()
                .gym(gym)
                .trainers(trainers == null ? null : trainers)
                .member(member)
                .bookingDate(bookingDate)
                .build();
    }
}
