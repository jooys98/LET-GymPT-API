package com.example.gympt.domain.reverseAuction.repository.querydsl;

import com.example.gympt.domain.member.entity.Member;
import com.example.gympt.domain.reverseAuction.entity.AuctionTrainerBid;

import java.util.List;

public interface AuctionTrainerBidRepositoryCustom {
    List<Member> findMemberTrainerInAuction(Long auctionId);


}
