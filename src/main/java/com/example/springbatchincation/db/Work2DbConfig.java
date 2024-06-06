package com.example.springbatchincation.db;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class Work2DbConfig {

    @Bean("work2Db")
    public DataSource work2Db() {
        DataSourceBuilder builder = DataSourceBuilder.create();
        builder.type(HikariDataSource.class);
        builder.driverClassName("com.mysql.cj.jdbc.Driver");
        builder.username("root");
        builder.password("1234");
        builder.url("jdbc:mysql://localhost:3306/n_cms");
        return builder.build();
    }

    @Bean("work2Tr")
    protected PlatformTransactionManager work2TransactionManager() {
        return new DataSourceTransactionManager(work2Db());
    }
}
