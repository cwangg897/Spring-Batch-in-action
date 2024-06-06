package com.example.springbatchincation.job;

import com.example.springbatchincation.domain.Member;
import com.example.springbatchincation.domain.Product;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.boot.autoconfigure.batch.JobLauncherApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;

@Slf4j
@Configuration
@EnableConfigurationProperties(BatchProperties.class)
public class TestJobConfiguration extends DefaultBatchConfiguration {

    private final int CHUNK_SIZE = 10;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "spring.batch.job", name = "enabled", havingValue = "true", matchIfMissing = true)
    public JobLauncherApplicationRunner jobLauncherApplicationRunner(JobLauncher jobLauncher, JobExplorer jobExplorer,
                                                                     JobRepository jobRepository, BatchProperties properties) {
        JobLauncherApplicationRunner runner = new JobLauncherApplicationRunner(jobLauncher, jobExplorer, jobRepository);
        String jobNames = properties.getJob().getName();
        if (StringUtils.hasText(jobNames)) {
            runner.setJobName(jobNames);
        }
        return runner;
    }

    @Primary
    @Bean("dataSource")
    public DataSource springBatchDb() {
        DataSourceBuilder builder = DataSourceBuilder.create();
        builder.type(HikariDataSource.class);
        builder.driverClassName("com.mysql.cj.jdbc.Driver");
        builder.username("root");
        builder.password("1234");
        builder.url("jdbc:mysql://localhost:3306/choi");
        return builder.build();
    }

    @Primary
    @Bean("transactionManager")
    protected PlatformTransactionManager getTransactionManager() {
        return new DataSourceTransactionManager(springBatchDb());
    }


    @Bean
    public Job job(JobRepository jobRepository, Step testStep109, Step nextStep) {
        return new JobBuilder("testJob109", jobRepository)
                .start(testStep109)
                .next(nextStep)
                .build();
    }

    @Bean
    public Step testStep109(
            JobRepository jobRepository,
            PlatformTransactionManager platformTransactionManager,
            @Qualifier(value = "workDb") DataSource dataSource) {
        return new StepBuilder("testStep", jobRepository)
                .<Product, Product>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(jdbcCursorItemReader(dataSource))
                .writer(jdbcCursorItemWriter())
                .build();
    }

    @Bean
    public JdbcCursorItemReader<Product> jdbcCursorItemReader(DataSource dataSource){
        JdbcCursorItemReader<Product> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setFetchSize(CHUNK_SIZE);
        reader.setSql("SELECT id, category_id, name, price stock, created_at, updated_at FROM product");
        reader.setName("jdbcCursorItemReader");
        reader.setRowMapper(new BeanPropertyRowMapper<>(Product.class));
        return reader;
    }


    @Bean
    public Step nextStep(JobRepository jobRepository,
                      PlatformTransactionManager platformTransactionManager,
                      @Qualifier(value = "work2Db") DataSource dataSource) {
        return new StepBuilder("step2", jobRepository)
                .<Member, Member>chunk(CHUNK_SIZE, platformTransactionManager)
                .reader(jdbc2CursorItemReader(dataSource))
                .writer(jdbc2CursorItemWriter())
                .build();
    }
    @Bean
    public JdbcCursorItemReader<Member> jdbc2CursorItemReader(DataSource dataSource){
        JdbcCursorItemReader<Member> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setFetchSize(CHUNK_SIZE);
        reader.setSql("SELECT id, email, password FROM member");
        reader.setName("jdbcCursorItemReader");
        reader.setRowMapper(new BeanPropertyRowMapper<>(Member.class));
        return reader;
    }

    private ItemWriter<Member> jdbc2CursorItemWriter() {
        return list -> {
            for (Member pay: list) {
                System.out.println(pay);
            }
        };
    }


    private ItemWriter<Product> jdbcCursorItemWriter() {
        return list -> {
            for (Product pay: list) {
                System.out.println(pay);
            }
        };
    }
}
