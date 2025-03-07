package com.example.gympt.domain.reverseAuction.repository.querydsl;


import com.example.gympt.domain.member.entity.Member;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.gympt.domain.member.entity.QMember.member;
import static com.example.gympt.domain.reverseAuction.entity.QAuctionTrainerBid.auctionTrainerBid;
import static com.example.gympt.domain.trainer.entity.QTrainers.trainers;

@RequiredArgsConstructor
public class AuctionTrainerBidRepositoryCustomImpl implements AuctionTrainerBidRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Transactional
    @Override
    public List<Member> findMemberTrainerInAuction(Long auctionId) {
        return queryFactory
                .select(member)
                .from(auctionTrainerBid) //역경매에 참여한 트레이너 들
                .join(auctionTrainerBid.trainer, trainers) //트레이너 엔티티 조인
                .join(trainers.member, member) // 트레이너 - member 엔티티 조인
                .where(auctionTrainerBid.auctionRequest.id.eq(auctionId)) // 역경매에 참여한 트레이너 멤버들 전체
                .distinct()
                .fetch();
    }


}
