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

    /**
     * @param landmarkId
     * @param memberId
     * fetchOne : 단건을 조회할 때 사용하는 방법, 결과가 없을때는 null 반환, 둘 이상일 떄는 NonUniqueResultException
     * fetchFirst : 처음의 한 건을 쿼리에서 가져오고 싶을 떄
     * */
    public Optional<AuctionLog> findByLandmarkAndMember(Long memberId, Long landmarkId) {
        return Optional.ofNullable(
                queryFactory.selectFrom(auctionLog)
                        .join(auctionLog.auction, auction)
                        .where(
                                auction.finished.isFalse(),
                                auctionLog.member.id.eq(memberId),
                                auction.landmark.id.eq(landmarkId)
                        )
                        .fetchOne()
        );
    }

    /**
     * @param landmarkId 해당 랜드마크에 등록된 가장 첫번째 입찰 기록 리턴
     * */
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
