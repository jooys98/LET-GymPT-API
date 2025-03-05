package com.example.gympt.domain.reverseAuction.repository.querydsl;


import com.example.gympt.domain.member.entity.Member;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.example.gympt.domain.member.entity.QMember.member;
import static com.example.gympt.domain.reverseAuction.entity.QAuctionTrainerBid.auctionTrainerBid;
import static com.example.gympt.domain.trainer.entity.QTrainers.trainers;

@RequiredArgsConstructor
public class AuctionTrainerBidRepositoryCustomImpl implements AuctionTrainerBidRepositoryCustom {
private final JPAQueryFactory queryFactory;

    @Override
    public List<Member> findMemberTrainerInAuction(Long auctionId) {
        return queryFactory
                .select(member)
                .from(auctionTrainerBid)
                .join(auctionTrainerBid.trainer, trainers)
                .join(trainers.member , member).fetchJoin()
                .where(auctionTrainerBid.auctionRequest.id.eq(auctionId))
                .distinct()
                .fetch();
    }


}
