package com.example.gympt.domain.member.repository.querydsl;

import com.example.gympt.domain.category.entity.Local;
import com.example.gympt.domain.category.entity.enums.LastType;
import com.example.gympt.domain.category.repository.LocalRepository;
import com.example.gympt.domain.member.entity.Member;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.gympt.domain.category.entity.QLocal.local;
import static com.example.gympt.domain.category.entity.enums.LastType.Y;
import static com.example.gympt.domain.member.entity.QMember.member;
import static com.example.gympt.domain.trainer.entity.QTrainers.trainers;

@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final LocalRepository localRepository;

    @Override
    @Transactional
    public List<Member> findMemberTrainerInLocal(Long localId) {
        Local targetLocal = localRepository.findById(localId)
                .orElseThrow(() -> new EntityNotFoundException("지역을 찾을 수 없습니다: " + localId));

        return queryFactory
                .selectFrom(member)
                .distinct()
                .join(member.trainer, trainers)
                .join(trainers.local, local)
                .where(eqInLocal(targetLocal))
                .fetch();
    }






    private BooleanExpression eqInLocal(Local targetLocal) {
        if (targetLocal == null) {
            return null;
        }
        // 대상 지역이거나 (관악구)
        return local.id.eq(targetLocal.getId())
                //대상 지역의 자식 지역 (서울대입구)
                .or(local.parent.id.eq(targetLocal.getId()));
    }
}