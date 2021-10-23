package com.example.config.db;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;


@Configuration
public class SecondDbConfig {
    @Bean(name = "secondSource")
    @ConfigurationProperties("spring.seconddb")
    public DataSource secondaryDataSource() {
        return DataSourceBuilder.create().build();
    }


    @Bean(name = "jdbcSecond")
    @Autowired
    public JdbcTemplate secondaryJdbcTemplate(@Qualifier("secondSource") DataSource dsMaster) {
        return new JdbcTemplate(dsMaster);
    }


}
