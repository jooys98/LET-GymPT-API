package com.example.gympt.domain.trainer.repository.querydsl;

import com.example.gympt.domain.category.entity.Local;
import com.example.gympt.domain.trainer.dto.TrainerRequestDTO;
import com.example.gympt.domain.trainer.entity.Trainers;
import com.example.gympt.domain.trainer.enums.Gender;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.example.gympt.domain.trainer.entity.QTrainers.trainers;

@RequiredArgsConstructor
public class TrainerRepositoryCustomImpl implements TrainerRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Trainers> findTrainers(TrainerRequestDTO trainerRequestDTO, Pageable pageable) {
        Gender gender = null;
        String genderStr = trainerRequestDTO.getGender();

        if (genderStr != null && !genderStr.isEmpty()) {
            // "M"이면 Gender.M, 아니면 Gender.F (또는 안전한 기본값 처리)
            if (genderStr.equals("M")) {
                gender = Gender.M;
            } else if (genderStr.equals("F")) {
                gender = Gender.F;
            }
            // 다른 값은 null 처리
        }


        return jpaQueryFactory
                .selectFrom(trainers)
                .where(
                        containsSearchKeyword(trainerRequestDTO.getSearchKeyword()),
                        betweenAge(trainerRequestDTO.getMinAge(), trainerRequestDTO.getMaxAge()),
                        filterByGender(gender),
                        filterByName(trainerRequestDTO.getName()),
                        filterByGymName(trainerRequestDTO.getGymName()),
                        filterByLocal(trainerRequestDTO.getLocal())
                ).offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(trainers.id.desc())
                .fetch();
    }

    private BooleanExpression  filterByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }
        return trainers.trainerName.contains(name);
    }

    private BooleanExpression filterByGymName(String gymName) {
        if (gymName == null || gymName.trim().isEmpty()) {
            return null;
        }
        return trainers.gym.gymName.contains(gymName);
    }

    //단어검색
    private BooleanExpression containsSearchKeyword(String searchKeyword) {
        if (searchKeyword == null || searchKeyword.trim().isEmpty()) {
            return null;
        }
        return trainers.trainerName.contains(searchKeyword)
                .or(trainers.introduction.contains(searchKeyword));

    }

    //나이 범위 검색
    private BooleanExpression betweenAge(Long minAge, Long maxAge) {
        if (minAge == null && maxAge == null) {
            return null;
        }
        if (minAge == null) {
            return trainers.age.loe(maxAge);
        }
        if (maxAge == null) {
            return trainers.age.goe(minAge);
        }
        return trainers.age.between(minAge, maxAge);
    }

    //성별 검색
    private BooleanExpression filterByGender(Gender gender) {
        return gender != null ? trainers.gender.eq(gender) : null;
    }

    //지역 검색
    private BooleanExpression filterByLocal(String local) {
        if (local == null || local.isEmpty()) {
            return null;
        }
        return trainers.local.localName.eq(local);
    }

    @Override
    public Long countTrainers(TrainerRequestDTO trainerRequestDTO) {
        Gender gender = null;
        String genderStr = trainerRequestDTO.getGender();

        if (genderStr != null && !genderStr.isEmpty()) {
            // "M"이면 Gender.M, 아니면 Gender.F (또는 안전한 기본값 처리)
            if (genderStr.equals("M")) {
                gender = Gender.M;
            } else if (genderStr.equals("F")) {
                gender = Gender.F;
            }
            // 다른 값은 null로 처리 (모든 성별 검색)
        }
        return jpaQueryFactory
                .select(trainers.count())
                .from(trainers)
                .where(
                        containsSearchKeyword(trainerRequestDTO.getSearchKeyword()),
                        betweenAge(trainerRequestDTO.getMinAge(), trainerRequestDTO.getMaxAge()),
                        filterByGender(gender),
                        filterByName(trainerRequestDTO.getName()),
                        filterByGymName(trainerRequestDTO.getGymName()),
                        filterByLocal(trainerRequestDTO.getLocal())
                )
                .fetchOne();
    }
}
