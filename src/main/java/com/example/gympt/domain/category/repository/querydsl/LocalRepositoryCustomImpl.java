package com.example.gympt.domain.category.repository.querydsl;

import com.example.gympt.domain.category.entity.Local;
import com.example.gympt.domain.category.entity.LocalGymBridge;
import com.example.gympt.domain.category.entity.QLocalGymBridge;
import com.example.gympt.domain.gym.entity.Gym;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.gympt.domain.category.entity.QLocal.local;
import static com.example.gympt.domain.category.entity.QLocalGymBridge.localGymBridge;
import static com.example.gympt.domain.category.entity.enums.LastType.Y;
import static com.example.gympt.domain.gym.entity.QGym.gym;

@RequiredArgsConstructor
public class LocalRepositoryCustomImpl implements LocalRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Local> findAllLocal() {
        return queryFactory
                .selectFrom(local)
                .where(local.parent.id.isNull())
                .fetch();
    }

    //지역으로 지하철 역 조회
    @Override
    public List<Local> findByLocalId(Long localId) {
        return queryFactory
                .selectFrom(local)
                .where(local.parent.id.eq(localId))
                .fetch();
    }

    @Override
    public List<Gym> findGymByLocalId(Long localId) {
        return queryFactory
                .selectFrom(gym)
                .distinct()
                .join(gym.localGymBridgeList, localGymBridge)
                .join(localGymBridge.local, local)
                .where(eqSubLocalId(localId).or(eqLocalId(localId)))
                .fetch();
    }


    //파라미터로 받은 카테고리 아이디가 child 카테고리 일때 (최하위 카테고리 아이디로 gym 조회)
    private BooleanExpression eqSubLocalId(Long subLocalId) {
        if (subLocalId == null) {
            return null;
        }
        return local.parent.isNotNull().and( // parent 가 null 이 아니면 -> sub or child
                local.id.eq(subLocalId)).and( //카테고리 아이디 = 파라미터로 받은 childCategoryId
                local.lastType.eq(Y)); // child 카테고리는 lastType 이 Y 이므로
    }

    //파라미터로 받은 카테고리 아이디가 main local 카테고리 일때 (main 카테고리 아이디로 gym 조회)
    private BooleanExpression eqLocalId(Long localId) {
        if (localId == null) {
            return null;
        }
        return local.parent.isNotNull().and( // parent 가 null 이 아니면 -> sub or child
                local.parent.id.eq(localId) //카테고리 parent 값 = 파라미터로 받은 subCategoryId
        );        // 즉 subCategory 에 해당하는 child 카테고리의 product 들
    }
}