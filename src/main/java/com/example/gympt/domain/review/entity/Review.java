package com.example.gympt.domain.review.entity;

import com.example.gympt.domain.gym.entity.Gym;
import com.example.gympt.domain.member.entity.Member;
import com.example.gympt.domain.review.dto.ReviewRequestDTO;
import com.example.gympt.domain.trainer.entity.Trainers;
import com.example.gympt.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@SuperBuilder
@AllArgsConstructor
@Getter
@NoArgsConstructor
@Entity
@Table(name = "review_tbl")
public class Review extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String content;
    private Double rating;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email")
    private Member member;

    private String reviewImage;
    //fk
    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    @JoinColumn(name = "gym_id")
    private Gym gym;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id")
    private Trainers trainers;

    private boolean active;

    public void changeActive(boolean active) {
        this.active = active;
    }

    public static Review from(ReviewRequestDTO reviewRequestDTO, Member member, Gym gym, String reviewImage, Trainers trainers) {
        return Review.builder()
                .content(reviewRequestDTO.getContent())
                .member(member)
                .gym(gym)
                .createdAt(LocalDateTime.now())
                .trainers(trainers)
                .reviewImage(reviewImage)
                .rating(reviewRequestDTO.getRating())
                .build();
    }

}


