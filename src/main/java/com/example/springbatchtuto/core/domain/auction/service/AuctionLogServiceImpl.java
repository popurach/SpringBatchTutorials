package com.example.springbatchtuto.core.domain.auction.service;

import com.example.springbatchtuto.core.common.exception.ResourceNotFoundException;
import com.example.springbatchtuto.core.domain.auction.model.entity.Auction;
import com.example.springbatchtuto.core.domain.auction.model.entity.AuctionLog;
import com.example.springbatchtuto.core.domain.auction.repository.AuctionLogRepository;
import com.example.springbatchtuto.core.domain.auction.repository.AuctionRepository;
import com.example.springbatchtuto.core.domain.landmark.repository.LandmarkRepository;
import com.example.springbatchtuto.core.domain.member.model.entity.Member;
import com.example.springbatchtuto.core.domain.member.repository.MemberRepository;
import java.util.List;
import java.util.logging.Logger;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuctionLogServiceImpl implements AuctionLogService {

    private final AuctionLogRepository auctionLogRepository;
    private final AuctionRepository auctionRepository;
    private final LandmarkRepository landmarkRepository;
    private final MemberRepository memberRepository;
    private final Logger LOGGER = (Logger) LoggerFactory.getLogger(AuctionLogServiceImpl.class);

    @Override
    public void actionsBidding(Long memberId, Long landmarkId, int price) {

    }

    /**
     * @param memberId     유저 PK
     * @param auctionLogId 경매 데이터 PK
     */
    @Override
    public void auctionsCancelBidding(Long memberId, Long auctionLogId) {
        Member member = memberRepository.findById(memberId)
                                        .orElseThrow(() -> new ResourceNotFoundException("해당 리소스 존재하지 않습니다."));
        AuctionLog auctionLog = auctionLogRepository.findById(auctionLogId)
                                                    .orElseThrow(() -> new ResourceNotFoundException("\"해당 리소스 존재하지 않습니다."));

        // 포인트 돌려주기
        member.gainPoint(auctionLog.getPrice());
        auctionLogRepository.delete(auctionLog);

        // 최고 입찰자가 아닌 경우
        if(!auctionLogId.equals(auctionLog.getAuction().getLastLogId())) {
            return;
        }

        // 최고 입찰자인 경우
        Auction auction = auctionLog.getAuction();
    }

    @Override
    public void auctionExecute(AuctionLog auctionLog) {

    }

    @Override
    public List<Integer> auctionRecord(Long landmarkId) {
        return null;
    }

    @Override
    public Integer auctionBestPrice(Long landmarkId) {
        return null;
    }

}
