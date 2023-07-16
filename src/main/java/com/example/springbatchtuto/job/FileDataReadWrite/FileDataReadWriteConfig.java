package com.example.springbatchtuto.job.FileDataReadWrite;

import com.example.springbatchtuto.job.FileDataReadWrite.dto.Player;
import com.example.springbatchtuto.job.FileDataReadWrite.dto.PlayerYears;
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
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
/**
 * desc: 파일 읽고 쓰기
 * run: --spring.batch.job.names=fileReadWriteJob
 * */
@Configuration
@RequiredArgsConstructor
public class FileDataReadWriteConfig {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job fileReadWriteJob(Step fileReadWriteStep) {
        return jobBuilderFactory.get("fileReadWriteJob")
                .incrementer(new RunIdIncrementer())
                .start(fileReadWriteStep)
                .build();
    }

    @JobScope
    @Bean
    public Step fileReadWriteStep(ItemReader playerItemReader,
                                  ItemProcessor playerItemProcessor,
                                  ItemWriter playerItemWriter) {
        return stepBuilderFactory. get("fileReadWriteStep")
                .<Player, PlayerYears>chunk(5)
                .reader(playerItemReader)
//                .writer(new ItemWriter() {
//                            @Override
//                            public void write(List items) throws Exception {
//                                items.forEach(System.out::println);
//                            }
//                        })
                .processor(playerItemProcessor)
                .writer(playerItemWriter)
                .build();
    }
    @StepScope
    @Bean
    public ItemProcessor<Player, PlayerYears> playerItemProcessor() {
        return new ItemProcessor<Player, PlayerYears>() {
            @Override
            public PlayerYears process(Player item) throws Exception {
                return new PlayerYears(item);
            }
        };
    }

    @StepScope
    @Bean
    public FlatFileItemReader<Player> playerItemReader() {
        return new FlatFileItemReaderBuilder<Player>()
                .name("playerItemReader")
                .resource(new FileSystemResource("Player.csv")) // Player.csv 파일을 읽어옴
                .lineTokenizer(new DelimitedLineTokenizer()) // 콤마 단위로 나눠줌
                .fieldSetMapper(new PlayerFieldSetMapper()) // mapper 함수 정의
                .linesToSkip(1) // 첫째줄은 제목이므로 읽어오지 않음
                .build();
    }

    @StepScope
    @Bean
    public FlatFileItemWriter<PlayerYears> playerItemWriter() {
        // 어떤 필드를 사용할지 명시를 위해 BeanWrapperFieldExtractor 사용
        BeanWrapperFieldExtractor<PlayerYears> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"ID", "lastName", "position", "yearsExperience"});
        fieldExtractor.afterPropertiesSet();

        // 어떤 기준으로 파일을 만들어주는지 알려주기 위해서
        DelimitedLineAggregator<PlayerYears> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(","); // 다시 csv 파일로 생성할 것이므로 ,로
        lineAggregator.setFieldExtractor(fieldExtractor);

        FileSystemResource outputResource = new FileSystemResource("players_output.txt");

        return new FlatFileItemWriterBuilder<PlayerYears>()
                .name("playerItemWriter")
                .resource(outputResource)
                .lineAggregator(lineAggregator)
                .build();
    }
}
