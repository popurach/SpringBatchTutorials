package com.example.springbatchtuto.core.domain.auction.service;

import com.example.springbatchtuto.core.common.exception.ResourceNotFoundException;
import com.example.springbatchtuto.core.domain.auction.model.entity.Auction;
import com.example.springbatchtuto.core.domain.auction.model.entity.AuctionLog;
import com.example.springbatchtuto.core.domain.auction.repository.AuctionLogCustomQueryRepository;
import com.example.springbatchtuto.core.domain.auction.repository.AuctionLogRepository;
import com.example.springbatchtuto.core.domain.auction.repository.AuctionRepository;
import com.example.springbatchtuto.core.domain.landmark.model.entity.Landmark;
import com.example.springbatchtuto.core.domain.landmark.repository.LandmarkRepository;
import com.example.springbatchtuto.core.domain.member.model.entity.Member;
import com.example.springbatchtuto.core.domain.member.repository.MemberRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuctionLogServiceImpl implements AuctionLogService {

    private final AuctionLogRepository auctionLogRepository;
    private final AuctionLogCustomQueryRepository auctionLogCustomQueryRepository;
    private final AuctionRepository auctionRepository;
    private final LandmarkRepository landmarkRepository;
    private final MemberRepository memberRepository;
//    private final Logger LOGGER = (Logger) LoggerFactory.getLogger(AuctionLogServiceImpl.class);

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
        if (!auctionLogId.equals(auctionLog.getAuction().getLastLogId())) {
            return;
        }

        // 최고 입찰자인 경우
        Auction auction = auctionLog.getAuction();
        Optional<AuctionLog> auctionLogMax = auctionLogCustomQueryRepository.findFirstByLandmarkId(auction.getLandmark().getId());
        if (auctionLogMax.isPresent()) { // 최고 입찰 기록 update
            auction.changeLastLogId(auctionLogMax.get().getId());
        } else {
            auction.changeLastLogId(null);
        }
//        Optional.ofNullable(auctionLogCustomQueryRepository.findFirstByLandmarkId(auction.getLandmark().getId()))
//                                    .ifPresentOrElse(
//                                            auctionLogMax -> auction.changeLastLogId(auctionLogMax.get().getId()),
//                                            () -> auction.changeLastLogId(null)
//                                    );
    }

    /**
     * @param auctionLog 해당 경매 기록
     * 낙찰자 -> 해당 랜드마크 주인으로 변경
     * 유찰자 -> 입찰 가격 환불 및 경매 기록 삭제
     */
    @Override
    public void auctionExecute(AuctionLog auctionLog) {
        Member member = auctionLog.getMember();
        Auction auction = auctionLog.getAuction();
        Landmark landmark = auction.getLandmark();
        Long succLastLogId = auction.getLastLogId();

        if(Boolean.TRUE.equals(auction.getFinished())) {
            throw new ResourceNotFoundException("해당 리소스는 끝난 경매 기록입니다.");
        }
        // 낙찰자 처리, succLastLogId null 체크
        if(succLastLogId != null && succLastLogId.equals(auctionLog.getId())) {
            landmark.changeOwner(member.getId());
        } else { // 유찰자 처리
            member.gainPoint(auctionLog.getPrice());
            auctionLogRepository.delete(auctionLog);
        }
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
