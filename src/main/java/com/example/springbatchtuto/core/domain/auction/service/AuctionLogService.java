package com.example.springbatchtuto.core.domain.auction.service;

import com.example.springbatchtuto.core.domain.auction.model.entity.AuctionLog;
import java.util.List;

public interface AuctionLogService {

    void actionsBidding(Long memberId, Long landmarkId, int price);

    void auctionsCancelBidding(Long memberId, Long auctionLogId);

    void auctionExecute(AuctionLog auctionLog);

    List<Integer> auctionRecord(Long landmarkId);

    Integer auctionBestPrice(Long landmarkId);

}
