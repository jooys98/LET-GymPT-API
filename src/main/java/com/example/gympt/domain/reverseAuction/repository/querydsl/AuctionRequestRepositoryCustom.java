package com.example.gympt.domain.reverseAuction.repository.querydsl;

import com.example.gympt.domain.category.entity.Local;
import com.example.gympt.domain.member.entity.Member;
import com.example.gympt.domain.reverseAuction.entity.AuctionRequest;
import org.apache.logging.log4j.simple.internal.SimpleProvider;

import java.util.List;
import java.util.Optional;

public interface AuctionRequestRepositoryCustom {
    Optional<AuctionRequest> findByProgressEmail(String email);

    List<AuctionRequest> findByAuctionInLocal(List<Local> localIds);
}
