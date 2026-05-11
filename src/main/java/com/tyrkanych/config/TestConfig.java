package com.tyrkanych.config;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = "com.tyrkanych")
@EnableAspectJAutoProxy          // ← AOP для тестів (LoggingAspect, TransactionAspect)
@EnableTransactionManagement     // ← @Transactional на сервісах працює в тестах
public class TestConfig {

    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                // MODE=MySQL — щоб SQL-синтаксис збігався з продакшном
                .setName("heartsync_test;MODE=MySQL;DB_CLOSE_DELAY=-1")
                .addScript("classpath:schema.sql")
                .build();
    }

    /**
     * JdbcTemplate — інжектується в BaseIntegrationTest для очищення таблиць.
     */
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    /**
     * TransactionManager — потрібен для коректної роботи @Transactional у сервісах під час тестів.
     */
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}