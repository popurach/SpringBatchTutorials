package com.example.springbatchtuto.core.domain.auction.repository;

import com.example.springbatchtuto.core.domain.auction.model.entity.AuctionLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuctionLogRepository extends JpaRepository<AuctionLog, Long> {

}
