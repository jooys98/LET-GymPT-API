package com.example.gympt.domain.reverseAuction.repository;

import com.example.gympt.domain.reverseAuction.entity.MatchedAuction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchedAuctionRepository extends JpaRepository<MatchedAuction, Long> {
}
