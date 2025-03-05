package com.example.gympt.domain.gym.repository.queryDsl;

import com.example.gympt.domain.category.entity.QLocalGymBridge;
import com.example.gympt.domain.gym.dto.GymSearchRequestDTO;
import com.example.gympt.domain.gym.entity.Gym;
import com.example.gympt.domain.gym.enums.Popular;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.example.gympt.domain.category.entity.QLocal.local;
import static com.example.gympt.domain.category.entity.QLocalGymBridge.localGymBridge;
import static com.example.gympt.domain.gym.entity.QGym.gym;

@RequiredArgsConstructor
@Slf4j
public class GymRepositoryCustomImpl implements GymRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Gym> findByGym(GymSearchRequestDTO gymSearchRequestDTO, Pageable pageable) {
        return jpaQueryFactory
                .selectFrom(gym)
                .where(
                        eqLocal(gymSearchRequestDTO.getLocalName()),
                        containsSearchKeyword(gymSearchRequestDTO.getSearchKeyword()),
                        betweenDailyPrice(gymSearchRequestDTO.getMinDailyPrice(),
                                gymSearchRequestDTO.getMaxDailyPrice()),
                        betweenMonthlyPrice(gymSearchRequestDTO.getMinMonthlyPrice(),
                                gymSearchRequestDTO.getMaxMonthlyPrice()),
                        filterByPopular(gymSearchRequestDTO.getPopular())
                        //where절 안에는 boolean 만 들어가야 함!!
                )
                .offset(pageable.getOffset())     // 자동으로 페이지를 계산하여 시작위치 선정
                .limit(pageable.getPageSize())    // 한 페이지 당 요소 개수
                .orderBy(gym.id.desc())
                .fetch();
    }

    @Override
    public Long countByGym(GymSearchRequestDTO gymSearchRequestDTO) {
       return jpaQueryFactory
               .select(gym.count())
               .from(gym)
               .where(
                       eqLocal(gymSearchRequestDTO.getLocalName()),
                       containsSearchKeyword(gymSearchRequestDTO.getSearchKeyword()),
                       betweenDailyPrice(gymSearchRequestDTO.getMinDailyPrice(),
                               gymSearchRequestDTO.getMaxDailyPrice()),
                       betweenMonthlyPrice(gymSearchRequestDTO.getMinMonthlyPrice(),
                               gymSearchRequestDTO.getMaxMonthlyPrice()),
                       filterByPopular(gymSearchRequestDTO.getPopular())
               )
               .fetchOne();
    }


    //인기 헬스장 검색!
    private BooleanExpression filterByPopular(Popular isPopular) {
        return isPopular != null ? gym.popular.eq(isPopular) : null;
    }


    //지역별 검색
    private BooleanExpression eqLocal(String localName) {
        if (localName == null) {
            return null;
        }
        return gym.local.localName.contains(localName);
    }

    //단어 검색
    private BooleanExpression containsSearchKeyword(String searchKeyword) {
        if (searchKeyword == null || searchKeyword.trim().isEmpty()) {
            return null;
        }
        return gym.gymName.contains(searchKeyword)
                .or(gym.address.contains(searchKeyword))
                .or(gym.description.contains(searchKeyword));
    }

    //한달 이용권 가격 검색
    private BooleanExpression betweenMonthlyPrice(Long minPrice, Long maxPrice) {
        if (minPrice == null && maxPrice == null) {
            return null;
        }
        if (minPrice == null) {
            return gym.monthlyPrice.loe(maxPrice);
        }
        if (maxPrice == null) {
            return gym.monthlyPrice.goe(minPrice);
        }
        return gym.monthlyPrice.between(minPrice, maxPrice);
    }

    //하루 이용권 가격 검색
    private BooleanExpression betweenDailyPrice(Long minPrice, Long maxPrice) {
        if (minPrice == null && maxPrice == null) {
            return null;
        }
        if (minPrice == null) {
            return gym.dailyPrice.loe(maxPrice);
        }
        if (maxPrice == null) {
            return gym.dailyPrice.goe(minPrice);
        }
        return gym.dailyPrice.between(minPrice, maxPrice);
    }


    //카테고리 로 조건에 맞는 해당 프로덕트 들을 조회


}
