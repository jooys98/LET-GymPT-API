package com.example.gympt.domain.trainer.entity;

import com.example.gympt.domain.gym.entity.Gym;
import com.example.gympt.domain.gym.entity.GymImage;
import com.example.gympt.domain.member.entity.Member;
import com.example.gympt.domain.trainer.enums.Gender;
import com.example.gympt.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.apache.catalina.session.FileStore;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SuperBuilder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "trainer_form")
public class TrainerSaveForm extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String introduction;
    private Long age;
    private Gender gender;

    @ElementCollection
    @Builder.Default
    private List<TrainerSaveImage> imageList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gym_id")
    private Gym gym;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email")
    private Member member;

    public void addGender(String gender) {
        this.gender = gender.equals("M") ? Gender.M : Gender.F;
    }


    //이미지 추가 , 파라미터 : 이미지 ,
    public void addImage(TrainerSaveImage image) {

        image.setOrd(this.imageList.size());
        imageList.add(image);
    }

    //이미지 파일 이름으로 이미지 추가
    public static TrainerSaveImage addImageString(String fileName) {

        TrainerSaveImage image = TrainerSaveImage.builder()
                .trainerSaveImageName(fileName)
                .build();
        return image;
    }

    //이미지 삭제!!!!!!!!
    public void clearImageList() {
        this.imageList.clear();
    }

//문자열 리스트를 파라미터로 받음
//각 문자열을 TrainerSaveImage 객체로 변환
//변환된 객체들을 imageList 필드에 저장
}
