package com.example.springbatchincation.db;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class WorkDbConfig {

    @Bean("workDb")
    public DataSource workDb() {
        DataSourceBuilder builder = DataSourceBuilder.create();
        builder.type(HikariDataSource.class);
        builder.driverClassName("com.mysql.cj.jdbc.Driver");
        builder.username("root");
        builder.password("1234");
        builder.url("jdbc:mysql://localhost:3306/kusinsa");
        return builder.build();
    }

    @Bean("workTr")
    protected PlatformTransactionManager workTransactionManager() {
        return new DataSourceTransactionManager(workDb());
    }

}
