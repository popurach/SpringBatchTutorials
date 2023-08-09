package com.example.springbatchtuto.core.domain.auction.repository;

import com.example.springbatchtuto.core.domain.auction.model.entity.Auction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionRepository extends JpaRepository<Auction, Long> {

}
