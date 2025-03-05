package com.example.gympt.domain.reverseAuction.repository.querydsl;

import com.example.gympt.domain.category.entity.Local;
import com.example.gympt.domain.member.entity.Member;
import com.example.gympt.domain.reverseAuction.entity.AuctionRequest;
import com.example.gympt.domain.reverseAuction.enums.AuctionStatus;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.example.gympt.domain.category.entity.QLocal.local;
import static com.example.gympt.domain.member.entity.QMember.member;
import static com.example.gympt.domain.reverseAuction.entity.QAuctionRequest.auctionRequest;

@RequiredArgsConstructor
public class AuctionRequestRepositoryCustomImpl implements AuctionRequestRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<AuctionRequest> findByProgressEmail(String email) {
        return Optional.ofNullable(queryFactory
                .selectFrom(auctionRequest)
                .join(auctionRequest.member, member).fetchJoin()  // fetchJoin 적용
                .where(
                        eqEmail(email),
                        statusInProgress()
                )
                .fetchOne());
    }

    @Override
    public List<AuctionRequest> findByAuctionInLocal(List<Local> localIds) {
        return queryFactory
                .selectFrom(auctionRequest)
                .join(auctionRequest.local, local).fetchJoin()
                .where(
                        eqLocal(localIds)
                )
                .fetch();
    }

    private BooleanExpression eqLocal(List<Local> localIds) {
        if (localIds == null || localIds.isEmpty()) {
            return null;
        }
        return auctionRequest.local.in(localIds);
    }

    private BooleanExpression eqEmail(String email) {
        return email != null ? member.email.eq(email) : null;
    }

    private BooleanExpression statusInProgress() {
        return auctionRequest.status.contains(AuctionStatus.IN_PROGRESS);
    }
}
