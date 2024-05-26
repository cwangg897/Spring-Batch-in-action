package com.example.springbatchincation.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SimpleJobConfiguration {


    // Spring Batch에서 Job은 하나의 배치 작업 단위를 얘기하는데요.
    //Job 안에는 아래처럼 여러 Step이 존재하고, Step 안에 Tasklet 혹은 Reader & Processor & Writer 묶음이 존재합니다.
    @Bean
    public Job simpleJob(JobRepository jobRepository, Step simpleStep1, Step simpleStep2){
        return new JobBuilder("simpleJob", jobRepository)
                .start(simpleStep1)
                .next(simpleStep2)
                .build();
    }

    @Bean
    @JobScope
    public Step simpleStep1(
            @Value("#{jobParameters[requestDate]}") String requestDate,
            JobRepository jobRepository, Tasklet simpleTasklet1, PlatformTransactionManager transactionManager){
        return new StepBuilder("simpleStep1", jobRepository)
                .tasklet(simpleTasklet1, transactionManager)
                .build();
    }

    @Bean
    @JobScope
    public Step simpleStep2(
            @Value("#{jobParameters[requestDate]}") String requestDate,
            JobRepository jobRepository, Tasklet simpleTasklet2, PlatformTransactionManager transactionManager){
        return new StepBuilder("simpleStep2", jobRepository)
                .tasklet(simpleTasklet2, transactionManager)
                .build();
    }


    @Bean
    public Tasklet simpleTasklet2(){
        return ((contribution, chunkContext) -> {
            log.info(">>>>> This is Step2");
            return RepeatStatus.FINISHED;
        });
    }
    @Bean
    public Tasklet simpleTasklet1(){
        return ((contribution, chunkContext) -> {
            log.info(">>>>> This is Step1");
            return RepeatStatus.FINISHED;
        });
    }

}
