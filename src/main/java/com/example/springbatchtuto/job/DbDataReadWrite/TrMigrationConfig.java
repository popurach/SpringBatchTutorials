package com.example.springbatchtuto.job.DbDataReadWrite;

import com.example.springbatchtuto.core.domain.accounts.Accounts;
import com.example.springbatchtuto.core.domain.accounts.AccountsRepository;
import com.example.springbatchtuto.core.domain.orders.Orders;
import com.example.springbatchtuto.core.domain.orders.OrdersRepository;
import java.util.Arrays;
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

/**
 * desc: 주문 테이블 -> 정산 테이블 데이터 이관
 * run: --spring.batch.job.names=trMigrationJob
 * */
@Configuration
@RequiredArgsConstructor
public class TrMigrationConfig {
    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private AccountsRepository accountsRepository;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job trMigrationJob(Step trMigrationStep) {
        return jobBuilderFactory.get("trMigrationJob")
                                .incrementer(new RunIdIncrementer())
                                .start(trMigrationStep)
                                .build();
    }

    @JobScope
    @Bean
    public Step trMigrationStep(ItemReader trOrdersReader,
                                ItemProcessor trOrderProcessor,
                                ItemWriter trOrderWriter) {
        return stepBuilderFactory.get("trMigrationStep")
                .<Orders, Accounts>chunk(5)
                .reader(trOrdersReader)
//                .writer(new ItemWriter() {
//                    @Override
//                    public void write(List items) throws Exception {
//                        items.forEach(System.out::println);
//                    }
//                })
                .processor(trOrderProcessor)
                .writer(trOrderWriter)
                .build();
    }

//    @StepScope
//    @Bean
//    public RepositoryItemWriter<Accounts> trOrderWriter() {
//        return new RepositoryItemWriterBuilder<Accounts>()
//                .repository(accountsRepository)
//                .methodName("save")
//                .build();
//    }

    // RepositoryItemWriter를 사용하지 않고 직접 구현도 가능
    @StepScope
    @Bean
    public ItemWriter<Accounts> trOrdersWriter() {
        return new ItemWriter<Accounts>() {
            @Override
            public void write(List<? extends Accounts> items) throws Exception {
                items.forEach(item -> accountsRepository.save(item));
            }
        };
    }

    @StepScope
    @Bean
    public ItemProcessor<Orders, Accounts> trOrderProcessor() {
        return new ItemProcessor<Orders, Accounts>() {
            @Override
            public Accounts process(Orders item) throws Exception {
                return new Accounts(item);
            }
        };
    }

    @StepScope
    @Bean
    public RepositoryItemReader<Orders> trOrdersReader() {
        return new RepositoryItemReaderBuilder<Orders>()
                .name("trOrdersReader")
                .repository(ordersRepository)
                .methodName("findAll")
                .pageSize(5)
                .arguments(Arrays.asList())
                .sorts(Collections.singletonMap("id", Direction.ASC))
                .build();
    }


}
