package com.example.gympt.domain.reverseAuction.repository;

import com.example.gympt.domain.reverseAuction.entity.AuctionTrainerBid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AuctionTrainerBidRepository extends JpaRepository<AuctionTrainerBid, Long> {

    @Query("SELECT atb FROM AuctionTrainerBid atb " +
            "WHERE atb.auctionRequest.id = :auctionRequestId " +
            "AND atb.trainer.member.email = :trainerEmail")
    Optional<AuctionTrainerBid> findByAuctionRequestIdAndTrainer(
            @Param("auctionRequestId") Long auctionRequestId,
            @Param("trainerEmail") String trainerEmail
    );

    @Query("select t from AuctionTrainerBid t where t.trainer.member.email =:email")
    Optional<AuctionTrainerBid> findByEmail(@Param("email") String trainerEmail);
}
