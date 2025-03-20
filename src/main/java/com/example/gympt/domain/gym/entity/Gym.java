package com.example.gympt.domain.gym.entity;

import com.example.gympt.domain.category.entity.Local;
import com.example.gympt.domain.category.entity.LocalGymBridge;
import com.example.gympt.domain.gym.enums.Popular;
import com.example.gympt.domain.likes.entity.LikesGym;
import com.example.gympt.domain.review.entity.Review;
import com.example.gympt.domain.trainer.entity.TrainerImage;
import com.example.gympt.domain.trainer.entity.Trainers;
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
@ToString(exclude = {"imageList", "trainers"})
@Table(name = "gym_tbl")
public class Gym {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String gymName;
    private String address;
    @Column(name = "description", columnDefinition = "LONGTEXT")
    private String description;
    private Long dailyPrice;
    private Long monthlyPrice;
    private String info;
    @OneToMany(mappedBy = "gym", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LikesGym> likes = new ArrayList<>();

    @OneToMany(mappedBy = "gym", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "gym", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Trainers> trainers = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "local_id")
    private Local local;

    @OneToMany(mappedBy = "gym", cascade = CascadeType.ALL)
    @Builder.Default
    private List<LocalGymBridge> localGymBridgeList = new ArrayList<>();

    @Column(length = 1000)
    private String reviewSummary;


    @Enumerated(EnumType.STRING)
    @ColumnDefault("'N'")
    private Popular popular;


    public int getLikesCount() {
        return this.likes.size();
    } // 좋아요 카운트!!

    @ElementCollection
    @Builder.Default
    private List<GymImage> imageList = new ArrayList<>();
//이미지 이름이 저장되는 이미지 엔티티 !


    //이미지 추가 , 파라미터 : 이미지 ,
    public void addImage(GymImage image) {

        image.setOrd(this.imageList.size());
        imageList.add(image);
    }

    //이미지 파일 이름으로 이미지 추가
    public void addImageString(String fileName) {

        GymImage gymImage = GymImage.builder()
                .gymImageName(fileName)
                .build();
        addImage(gymImage);
    }

    public void addTrainer(Trainers trainer) {
        this.trainers.add(trainer);
    }

    public int trainerCount() {
        return this.trainers.size();
    }


    //이미지 삭제!!!!!!!!
    public void clearImageList() {
        this.imageList.clear();
    }

    //수정 로직(엔티티 관련 수정이 아닌 객체 관련 수정)
    public void updateGym(String gymName) {
        this.gymName = gymName;
    }

    public void updateLocal(Local local) {
        this.local = local;
    }

    // 요약 업데이트 메서드
    public void updateReviewSummary(String summary) {
        this.reviewSummary = summary;
    }

    public void addPopular(String popular) {
        this.popular = popular.equals("N") ? Popular.N : Popular.Y;
    }

    public void updateAddress(String address) {
        this.address = address;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateDailyPrice(Long dailyPrice) {
        this.dailyPrice = dailyPrice;
    }

    public void updateMonthlyPrice(Long monthlyPrice) {
        this.monthlyPrice = monthlyPrice;
    }

    public void updateTrainers(Trainers trainers) {
        this.trainers.add(trainers);
    }


    public double getReviewAverage() {
        if (reviews == null || reviews.isEmpty()) {
            return 0;
        }
        return reviews.stream().mapToDouble(Review::getRating).average().orElse(0);
    }

    public int getReviewCount() {
        return this.reviews.size();
    }

}
