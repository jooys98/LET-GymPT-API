package com.example.gympt.domain.likes.service;

import com.example.gympt.domain.gym.entity.Gym;
import com.example.gympt.domain.gym.entity.GymImage;
import com.example.gympt.domain.gym.repository.GymRepository;
import com.example.gympt.domain.likes.dto.LikesGymDTO;
import com.example.gympt.domain.likes.dto.LikesTrainersDTO;
import com.example.gympt.domain.likes.entity.LikesGym;
import com.example.gympt.domain.likes.entity.LikesTrainers;
import com.example.gympt.domain.likes.repository.LikesGymRepository;
import com.example.gympt.domain.likes.repository.LikesTrainerRepository;
import com.example.gympt.domain.member.entity.Member;
import com.example.gympt.domain.member.repository.MemberRepository;
import com.example.gympt.domain.trainer.entity.TrainerImage;
import com.example.gympt.domain.trainer.entity.Trainers;
import com.example.gympt.domain.trainer.repository.TrainerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class LikesServiceImpl implements LikesService {

    private final LikesGymRepository likesGymRepository;
    private final LikesTrainerRepository likesTrainerRepository;
    private final MemberRepository memberRepository;
    private final GymRepository gymRepository;
    private final TrainerRepository trainerRepository;

    /// @Param : 헬스장 아이디 , 유저 이메일
    @Override
    public Boolean toggleGymLikes(String email, Long gymId) {
        Member member = getMember(email);
        Gym gym = getGym(gymId);
        String likesMemberEmail = member.getEmail();
        Long likesGymId = gym.getId();

        Boolean likesResult = likesGymRepository.likes(likesMemberEmail, likesGymId);
        if (likesResult) {
            likesGymRepository.deleteEmailGymId(likesMemberEmail, likesGymId);
            return false;
        } else {
            LikesGym likesGym = LikesGym.builder()
                    .gym(gym)
                    .member(member)
                    .build();
            likesGymRepository.save(likesGym);
            return true;
        }
    }

    //헬스장 좋아요 조회
    @Override
    public List<LikesGymDTO> getLikesGymList(String email) {
        Member member = getMember(email);
        List<LikesGymDTO> likesGymList = likesGymRepository.findLikesGymsByMemberEmail(member.getEmail())
                .stream().map(likesGym -> this.toGymLikesDTO(likesGym, likesGymRepository.likes(email, likesGym.getId()))).toList();
        return likesGymList;
    }

    /// @Param: 트레이너 이메일 , 유저 이메일
    @Override
    public Boolean toggleTrainerLikes(String email, Long trainerId) {
        try {
            Member member = getMember(email);
            Trainers trainers = getTrainer(trainerId);
            String likesMemberEmail = member.getEmail();
            String likesTrainerEmail = trainers.getMember().getEmail();

            Boolean likesResult = likesTrainerRepository.existsByMember_EmailAndTrainers_Member_email(likesMemberEmail, likesTrainerEmail);
            if (likesResult) {
                likesTrainerRepository.deleteTrainerEmail(likesMemberEmail, likesTrainerEmail);
                return false;
            } else {
                LikesTrainers likesTrainers = LikesTrainers.builder()
                        .trainers(trainers)
                        .member(member)
                        .build();
                likesTrainerRepository.save(likesTrainers);
                return true;
            }

        } catch (Exception e) {
            throw new RuntimeException("트레이너 좋아요 실패 ㅜㅜ");
        }
    }

    //회원의 트레이너 좋아요 조회
    @Override
    public List<LikesTrainersDTO> getLikesTrainerList(String email) {
        Member member = getMember(email);
        List<LikesTrainersDTO> likesTrainersDTOS = likesTrainerRepository.findLikesTrainersByMemberEmail(member.getEmail())
                .stream().map(trainer -> this.toTrainerLikesDTO(trainer,likesTrainerRepository.likes(email, trainer.getId()))).toList();
        return likesTrainersDTOS;
    }


    private LikesTrainersDTO toTrainerLikesDTO(LikesTrainers likesTrainers,boolean likes) {
        Trainers trainers = getTrainer(likesTrainers.getTrainers().getId());
        String trainerImage = trainers.getImageList().stream().map(TrainerImage::getTrainerImageName).findFirst().orElse(null);

        LikesTrainersDTO likesTrainersDTO = LikesTrainersDTO.builder()
                .id(trainers.getId())
                .local(trainers.getLocal().getLocalName())
                .name(trainers.getTrainerName())
                .gender(trainers.getGender().toString())
                .email(likesTrainers.getMember().getEmail())
                .gymName(trainers.getGym().getGymName())
                .gymId(trainers.getGym().getId())
                .likesCount(trainers.getLikesCount())
                .trainerImage(trainerImage)
                .likes(likes)
                .build();
        return likesTrainersDTO;

    }

    private LikesGymDTO toGymLikesDTO(LikesGym likesGym, boolean likes) {
        Gym gym = getGym(likesGym.getId());
        String image = gym.getImageList().stream()
                .map(GymImage::getGymImageName)
                .findFirst().orElse(null);


        LikesGymDTO likesGymDTO = LikesGymDTO.builder()
                .id(gym.getId())
                .email(likesGym.getMember().getEmail())
                .gymName(gym.getGymName())
                .localName(gym.getLocal().getLocalName())
                .address(gym.getAddress())
                .dailyPrice(gym.getDailyPrice())
                .monthlyPrice(gym.getMonthlyPrice())
                .description(gym.getDescription())
                .likesCount(gym.getLikesCount())
                .popular(gym.getPopular())
                .gymImage(image)
                .likes(likes)
                .build();
        return likesGymDTO;
    }

    private Member getMember(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다"));
    }

    private Gym getGym(Long gymId) {
        return gymRepository.findByGymId(gymId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 헬스장 입니다 "));
    }

    private Trainers getTrainer(Long trainerId) {
        return trainerRepository.findById(trainerId).orElseThrow(() -> new EntityNotFoundException("트레이너가 존재하지 않습니다"));
    }

}
