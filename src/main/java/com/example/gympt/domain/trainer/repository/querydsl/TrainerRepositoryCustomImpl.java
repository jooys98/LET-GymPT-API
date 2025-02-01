package com.example.gympt.domain.trainer.repository.querydsl;

import com.example.gympt.domain.category.entity.Local;
import com.example.gympt.domain.trainer.dto.TrainerRequestDTO;
import com.example.gympt.domain.trainer.entity.Trainers;
import com.example.gympt.domain.trainer.enums.Gender;
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
        return jpaQueryFactory
                .selectFrom(trainers)
                .where(
                        containsSearchKeyword(trainerRequestDTO.getSearchKeyword()),
                        betweenAge(trainerRequestDTO.getMinAge(), trainerRequestDTO.getMaxAge()),
                        filterByGender(Gender.valueOf(trainerRequestDTO.getGender())),
                        filterByLocal(trainerRequestDTO.getLocal().toString())
                ).offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(trainers.id.desc())
                .fetch();
    }

    //단어검색
    private BooleanExpression containsSearchKeyword(String searchKeyword) {
        if (searchKeyword == null || searchKeyword.trim().isEmpty()) {
            return null;
        }
        return trainers.trainerName.contains(searchKeyword)
                .or(trainers.gym.gymName.contains(searchKeyword))
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
        return jpaQueryFactory
                .select(trainers.count())
                .from(trainers)
                .where(
                        containsSearchKeyword(trainerRequestDTO.getSearchKeyword()),
                        betweenAge(trainerRequestDTO.getMinAge(), trainerRequestDTO.getMaxAge()),
                        filterByGender(Gender.valueOf(trainerRequestDTO.getGender()))
                )
                .fetchOne();
    }
}
