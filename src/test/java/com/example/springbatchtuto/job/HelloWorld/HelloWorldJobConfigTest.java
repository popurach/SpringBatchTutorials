package com.example.springbatchtuto.job.HelloWorld;

import com.example.springbatchtuto.SpringBatchTestConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
// 하나의 Job은 하나의 테스트 가능
@ActiveProfiles("test")
//@ExtendWith(SpringExtension.class) // JUnit 5
@RunWith(SpringRunner.class) // JUnit 4
@SpringBatchTest
@SpringBootTest(classes = { SpringBatchTestConfig.class, HelloWorldJobConfig.class})
class HelloWorldJobConfigTest {
    // Given
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    public void success() throws Exception {
        // When
        JobExecution execution = jobLauncherTestUtils.launchJob();

        // Then
        Assertions.assertEquals(execution.getExitStatus(), ExitStatus.COMPLETED);
    }
}