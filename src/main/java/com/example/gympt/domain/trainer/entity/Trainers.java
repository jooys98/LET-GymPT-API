package com.example.gympt.domain.trainer.entity;

import com.example.gympt.domain.category.entity.Local;
import com.example.gympt.domain.gym.entity.Gym;
import com.example.gympt.domain.gym.enums.Popular;
import com.example.gympt.domain.likes.entity.Likes;
import com.example.gympt.domain.member.entity.Member;
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
    @Column(name = "introduction" , columnDefinition = "LONGTEXT")
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
    private List<Likes> likes = new ArrayList<>();

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

    public void clearImageList() {
        this.imageList.clear();
    }



}
