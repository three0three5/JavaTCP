package ru.hse.vectorizer.domain;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JdbcConfiguration {
    @Value("${database.username}")
    private final String username;

    @Value("${database.url}")
    private final String url;

    @Value("${database.password}")
    private final String password;

    private BasicDataSource ds;

    @Bean
    public DataSource dataSource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setMinIdle(5);
        ds.setMaxIdle(10);
        ds.setMaxOpenPreparedStatements(100);
        this.ds = ds;
        return ds;
    }

    @PreDestroy
    public void preDestroy() {
        if (ds == null) return;
        try {
            ds.close();
        } catch (SQLException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }
}
