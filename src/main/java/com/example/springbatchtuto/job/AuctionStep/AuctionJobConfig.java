package com.example.springbatchtuto.job.AuctionStep;

import com.example.springbatchtuto.core.common.exception.ResourceNotFoundException;
import com.example.springbatchtuto.core.domain.auction.model.entity.Auction;
import com.example.springbatchtuto.core.domain.auction.model.entity.AuctionLog;
import com.example.springbatchtuto.core.domain.auction.repository.AuctionLogCustomQueryRepository;
import com.example.springbatchtuto.core.domain.auction.repository.AuctionLogRepository;
import com.example.springbatchtuto.core.domain.auction.repository.AuctionRepository;
import com.example.springbatchtuto.core.domain.auction.service.AuctionLogService;
import com.example.springbatchtuto.core.domain.landmark.model.entity.Landmark;
import com.example.springbatchtuto.core.domain.landmark.repository.LandmarkRepository;
import com.example.springbatchtuto.core.domain.member.repository.MemberRepository;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

@Configuration
@RequiredArgsConstructor
public class AuctionJobConfig {

    @Autowired
    private AuctionLogService auctionLogService;

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private AuctionLogRepository auctionLogRepository;

    @Autowired
    private LandmarkRepository landmarkRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AuctionLogCustomQueryRepository auctionLogCustomQueryRepository;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job auctionJob(
            Step landmarksStep,
            Step auctionLogsStep,
            Step finishAuctionsStep,
            Step createAuctionsStep
    ) {
        return jobBuilderFactory.get("auctionJob")
                                .incrementer(new RunIdIncrementer())
                                .start(landmarksStep)
                                .next(auctionLogsStep)
                                .next(finishAuctionsStep)
                                .next(createAuctionsStep)
                                .build();
    }

    /**
     * 랜드마크 점유자 포인트 지급 및 점유자 null
     */
    @JobScope
    @Bean
    public Step landmarksStep(
            ItemReader<Landmark> landmarkReader,
            ItemProcessor<Landmark, Landmark> landmarkProcessor,
            ItemWriter<Landmark> landmarkWriter
    ) {
        return stepBuilderFactory.get("landmarkStep")
                                 .<Landmark, Landmark>chunk(5)
                                 .reader(landmarkReader)
                                 .processor(landmarkProcessor)
                                 .writer(landmarkWriter)
                                 .build();
    }

    @StepScope
    @Bean
    public ItemReader<Landmark> landmarkReader() {
        return new RepositoryItemReaderBuilder<Landmark>()
                .name("landmarkReader")
                .repository(landmarkRepository)
                .methodName("findAll")
                .pageSize(5)
                .arguments(List.of())
                .sorts(Collections.singletonMap("id", Sort.Direction.ASC))
                .build();
    }

    @StepScope
    @Bean
    public ItemProcessor<Landmark, Landmark> landmarkProcessor() {
        return new ItemProcessor<Landmark, Landmark>() {
            @Override
            public Landmark process(Landmark item) throws Exception {
                if (item.getMemberId() != 0L) {
                    memberRepository.findById(item.getMemberId())
                                    .ifPresentOrElse(
                                            member -> {
                                                member.gainPoint(1000);
                                            },
                                            () -> {
                                                throw new ResourceNotFoundException("회원을 찾을 수 없습니다.");
                                            }
                                    );
//                    memberRepository.findById(item.getMemberId()).get().gainPoint(1000);
                    item.changeOwner(0L);
                }
                return item;
            }
        };
    }

    @StepScope
    @Bean
    public ItemWriter<Landmark> landmarkWriter() {
        return new RepositoryItemWriterBuilder<Landmark>()
                .repository(landmarkRepository)
                .methodName("save")
                .build();
    }

    /**
     * 경매 입찰 테이블 확인 후 낙찰, 유찰 처리
     */
    @JobScope
    @Bean
    public Step auctionLogsStep(
            ItemReader<AuctionLog> auctionLogsReader,
            ItemProcessor<AuctionLog, AuctionLog> auctionLogsProcessor,
            ItemWriter<AuctionLog> auctionLogsWriter
    ) {
        return stepBuilderFactory.get("auctionLogsStep")
                                 .<AuctionLog, AuctionLog>chunk(5)
                                 .reader(auctionLogsReader)
                                 .processor(auctionLogsProcessor)
                                 .writer(auctionLogsWriter)
                                 .build();
    }

    @StepScope
    @Bean
    public RepositoryItemReader<AuctionLog> auctionLogsReader() {
        return new RepositoryItemReaderBuilder<AuctionLog>()
                .name("auctionLogsReader")
                .repository(auctionLogRepository)
                .methodName("findAll")
//                .methodName("findAllByAuction_Finished")
                .pageSize(5)
                .arguments(List.of())
//                .arguments(false)
                .sorts(Collections.singletonMap("id", Direction.ASC))
                .build();
    }

    @StepScope
    @Bean
    public ItemProcessor<AuctionLog, AuctionLog> auctionLogsProcessor() {
        return new ItemProcessor<AuctionLog, AuctionLog>() {
            @Override
            public AuctionLog process(AuctionLog item) throws Exception {
                auctionLogService.auctionExecute(item);
                return item;
            }
        };
    }

    @StepScope
    @Bean
    public ItemWriter<AuctionLog> auctionLogsWriter() {
        return new RepositoryItemWriterBuilder<AuctionLog>()
                .repository(auctionLogRepository)
                .methodName("save")
                .build();
    }

    @JobScope
    @Bean
    public Step finishAuctionsStep(
            ItemReader<Auction> auctionsReader,
            ItemProcessor<Auction, Auction> auctionsProcessor,
            ItemWriter<Auction> auctionsWriter
    ) {
        return stepBuilderFactory.get("finishAuctionStep")
                                 .<Auction, Auction>chunk(5)
                                 .reader(auctionsReader)
                                 .processor(auctionsProcessor)
                                 .writer(auctionsWriter)
                                 .build();
    }

    @StepScope
    @Bean
    public RepositoryItemReader<Auction> auctionsReader() {
        return new RepositoryItemReaderBuilder<Auction>()
                .name("auctionsReader")
                .repository(auctionRepository)
                .methodName("findAllByAuction_Finished")
                .pageSize(5)
                .arguments(false)
                .sorts(Collections.singletonMap("id", Direction.ASC))
                .build();
    }

    @StepScope
    @Bean
    public ItemProcessor<Auction, Auction> auctionsProcessor() {
        return item -> {
            item.setFinished(true);
            return item;
        };
    }

    @StepScope
    @Bean
    public RepositoryItemWriter<Auction> auctionsWriter() {
        return new RepositoryItemWriterBuilder<Auction>()
                .repository(auctionRepository)
                .methodName("save")
                .build();
    }

    @JobScope
    @Bean
    public Step createAuctionsStep(
            ItemReader<Landmark> landmarkReader,
            ItemProcessor<Landmark, Auction> auctionsCreateProcessor,
            ItemWriter<Auction> auctionsWriter
    ) {
        return stepBuilderFactory.get("createAuctionsStep")
                                 .<Landmark, Auction>chunk(5)
                                 .reader(landmarkReader)
                                 .processor(auctionsCreateProcessor)
                                 .writer(auctionsWriter)
                                 .build();
    }

    @StepScope
    @Bean
    public ItemProcessor<Landmark, Auction> auctionsCreateProcessor() {
        return new ItemProcessor<Landmark, Auction>() {
            @Override
            public Auction process(Landmark item) throws Exception {
                return Auction.builder()
                              .createdDate(LocalDate.now())
                              .landmark(item)
                              .build();
            }
        };
    }

}
