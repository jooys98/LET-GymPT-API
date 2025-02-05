package com.example.gympt.domain.reverseAuction.repository;

import com.example.gympt.domain.reverseAuction.entity.AuctionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AuctionRequestRepository extends JpaRepository<AuctionRequest, Long> {

    Boolean existsByMember_Email(String email);

    @Query("select m from AuctionRequest m where m.member.email = :email")
    Optional<AuctionRequest> findByEmail(@Param("email") String email);
}
