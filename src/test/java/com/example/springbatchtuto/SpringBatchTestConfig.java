package com.example.springbatchtuto;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
/**
 * SpringBatchTestConfig.class 파일 위치의 중요성
 * com.example.springbatchtuto.job에 있으면 오류 발생
 * -> 해당 패키지 위치부터 스프링 프레임워크가 스캔을 해서 빈을 등록을 하기 떄문에
 * com.example.springbatchtuto.job 하위에는 OrderRepository 를 찾을 수 없어서 의존성 주입이 불가
 * */
@Configuration
@EnableAutoConfiguration
@EnableBatchProcessing
public class SpringBatchTestConfig {

}
