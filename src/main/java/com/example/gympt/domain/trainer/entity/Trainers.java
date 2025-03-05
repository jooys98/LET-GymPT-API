package com.example.gympt.domain.trainer.entity;

import com.example.gympt.domain.booking.entity.Booking;
import com.example.gympt.domain.category.entity.Local;
import com.example.gympt.domain.gym.entity.Gym;
import com.example.gympt.domain.gym.enums.Popular;
import com.example.gympt.domain.likes.entity.LikesTrainers;
import com.example.gympt.domain.likes.entity.LikesTrainers;
import com.example.gympt.domain.member.entity.Member;
import com.example.gympt.domain.reverseAuction.entity.AuctionRequest;
import com.example.gympt.domain.review.entity.Review;
import com.example.gympt.domain.trainer.enums.Gender;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Entity
@ToString(exclude = "imageList")
@Table(name = "trainers_tbl")
public class Trainers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String trainerName;
    private Long age;
    @Column(name = "introduction", columnDefinition = "LONGTEXT")
    private String introduction;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "local_id")
    private Local local;

    @Enumerated(EnumType.STRING)
    @ColumnDefault("'M'")
    private Gender gender;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gym_id")
    private Gym gym;

    @OneToOne
    @JoinColumn(name = "email")
    private Member member;

    @OneToMany(mappedBy = "trainers", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "trainers", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> booking = new ArrayList<>();

    @OneToMany(mappedBy = "trainers", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LikesTrainers> likes = new ArrayList<>();
    //트레이너 좋아요

    public int getLikesCount() {
        return this.likes.size();
    }


    @ElementCollection
    @Builder.Default
    private List<TrainerImage> imageList = new ArrayList<>();
//TrainerImage 객체의 리스트 -> string 으로 변환 -> dto

    public void addImage(TrainerImage image) {
// 이미지 이름 리스트로 받아서 엔티티에 저장
        image.setOrd(this.imageList.size());
        imageList.add(image);
    }


    public void addImageString(String fileName) {
//이미지 이름 문자열 파라미터로  받아서 엔티티에  저장
        TrainerImage trainerImage = TrainerImage.builder()
                .trainerImageName(fileName)
                .build();
        addImage(trainerImage);
    }


    public void addGender(String gender) {
        this.gender = gender.equals("M") ? Gender.M : Gender.F;
    }

    public void updateTrainerName(String trainerName) {
        this.trainerName = trainerName;
    }

    public void updateAge(Long age) {
        this.age = age;
    }

    public void updateGender(Gender gender) {
        this.gender = gender;
    }

    public void updateLocal(Local local) {
        this.local = local;
    }

    public void updateGym(Gym gym) {
        this.gym = gym;
    }


    public void updateIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public void updateMember(Member member) {
        this.member = member;
    }


    public void clearImageList() {
        this.imageList.clear();
    }



}
