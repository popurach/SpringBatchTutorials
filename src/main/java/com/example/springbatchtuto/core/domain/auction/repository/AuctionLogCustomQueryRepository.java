package com.example.springbatchtuto.core.domain.auction.repository;

import static com.example.springbatchtuto.core.domain.auction.model.entity.QAuction.auction;
import static com.example.springbatchtuto.core.domain.auction.model.entity.QAuctionLog.auctionLog;

import com.example.springbatchtuto.core.domain.auction.model.entity.AuctionLog;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
@RequiredArgsConstructor
public class AuctionLogCustomQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Optional<AuctionLog> findFirstByLandmarkId(Long landmarkId) {
        return Optional.ofNullable(
                queryFactory.selectFrom(auctionLog)
                        .join(auctionLog.auction, auction).fetchJoin()
                        .where(
                                auction.finished.isFalse(),
                                auction.landmark.id.eq(landmarkId)
                        )
                        .orderBy(auctionLog.price.desc())
                        .fetchFirst()
        );
    }

}
