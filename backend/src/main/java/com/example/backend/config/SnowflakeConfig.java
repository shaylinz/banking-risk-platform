package com.example.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class SnowflakeConfig {

    @Value("${snowflake.url}")
    private String snowflakeUrl;

    @Value("${snowflake.username}")
    private String snowflakeUsername;

    @Value("${snowflake.password}")
    private String snowflakePassword;

    @Value("${snowflake.database}")
    private String snowflakeDatabase;

    @Value("${snowflake.schema}")
    private String snowflakeSchema;

    @Value("${snowflake.warehouse}")
    private String snowflakeWarehouse;

    @Bean(name = "snowflakeDataSource")
    public DataSource snowflakeDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName("net.snowflake.client.jdbc.SnowflakeDriver");
        dataSource.setJdbcUrl(snowflakeUrl);
        dataSource.setUsername(snowflakeUsername);
        dataSource.setPassword(snowflakePassword);

        // Set Snowflake-specific properties
        dataSource.addDataSourceProperty("db", snowflakeDatabase);
        dataSource.addDataSourceProperty("schema", snowflakeSchema);
        dataSource.addDataSourceProperty("warehouse", snowflakeWarehouse);
        dataSource.addDataSourceProperty("role", "PUBLIC"); // or your specific role

        // Connection pool settings
        dataSource.setMaximumPoolSize(10);
        dataSource.setMinimumIdle(2);
        dataSource.setConnectionTimeout(30000);
        dataSource.setIdleTimeout(600000);
        dataSource.setMaxLifetime(1800000);

        return dataSource;
    }

    @Bean(name = "snowflakeJdbcTemplate")
    public JdbcTemplate snowflakeJdbcTemplate() {
        return new JdbcTemplate(snowflakeDataSource());
    }
}
