package com.example.gympt.domain.trainer.dto;

import com.example.gympt.domain.trainer.entity.TrainerImage;
import com.example.gympt.domain.trainer.entity.Trainers;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class TrainerResponseDTO {
    private Long id;
    private String email;
    private String profileImage;
    private String name;
    private String introduction;
    private Long age;
    private Long gymId;
    private String gymName;
    private String gymAddress;
    private String gender;
    private Long localId;
    private String parentLocal;
    private String childrenLocal;
    private String local;
    private int likesCount;
    private boolean likes;
    private List<String> imageList;


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt; // 생성시간
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modifiedAt; // 수정 시간


    //트레이너 정보 조회시 쓰이는 dto 변환 메서드
    public static TrainerResponseDTO from(Trainers trainers, boolean likes) {

        List<String> imageNames = trainers.getImageList().stream()
                .map(TrainerImage::getTrainerImageName)
                .toList();
//Trainers 엔티티 속 이미지 리스트 객체를  가져와서 문자열 리스트로 만들어주기
        TrainerResponseDTO responseDTO = TrainerResponseDTO.builder()
                .id(trainers.getId())
                .profileImage(trainers.getProfileImage())
                .email(trainers.getMember().getEmail())
                .age(trainers.getAge())
                .name(trainers.getMember().getName())
                .gender(trainers.getGender().toString())
                .introduction(trainers.getIntroduction())
                .gymId(trainers.getGym().getId())
                .localId(trainers.getLocal().getId())
                .gymName(trainers.getGym().getGymName())
                .gymAddress(trainers.getGym().getAddress())
                .local(trainers.getLocal().getLocalName())
                .parentLocal(trainers.getLocal().getParent().getLocalName())
                .childrenLocal(trainers.getLocal().getChildren().toString())
                .likesCount(trainers.getLikesCount())
                .imageList(imageNames)
                .likes(likes)
                .build();
        return responseDTO;

    }

}
