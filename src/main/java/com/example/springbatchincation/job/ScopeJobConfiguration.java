package com.example.springbatchincation.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ScopeJobConfiguration {

    @Bean
    public Job scopeJob(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        return new JobBuilder("scopeJob", jobRepository)
                .start(scopeStep1(jobRepository, platformTransactionManager))
                .build();
    }

    @Bean
    public Step scopeStep1(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager){
        return new StepBuilder("scopeStep1", jobRepository)
                .tasklet((contribution, chunkContext) ->
                {
                    System.out.println("This is scopeStep1");
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager).build();
    }






}
