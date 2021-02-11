package com.example.batch.copy;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean(name = "h2EmbeddedDataSource")
    @Primary
    public DataSource h2EmbeddedDataSource() {
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
        return builder
                .setType(EmbeddedDatabaseType.H2)
                .build();
    }

    @Bean
    @ConfigurationProperties("source.datasource")
    public DataSourceProperties sourceDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean("source.datasource")
    @ConfigurationProperties("source.datasource")
    public DataSource sourceDataSource() {
        return sourceDataSourceProperties().initializeDataSourceBuilder()
                .type(HikariDataSource.class).build();
    }

    @Bean
    @ConfigurationProperties("destination.datasource")
    public DataSourceProperties destinationDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean("destination.datasource")
    @ConfigurationProperties("destination.datasource")
    public DataSource destinationDataSource() {
        return destinationDataSourceProperties().initializeDataSourceBuilder()
                .type(HikariDataSource.class).build();
    }

    @Bean
    @ConfigurationProperties("preprocess.datasource")
    public DataSourceProperties preprocessDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean("preprocess.datasource")
    @ConfigurationProperties("preprocess.datasource")
    public DataSource preprocessDataSource() {
        return sourceDataSourceProperties().initializeDataSourceBuilder()
                .type(HikariDataSource.class).build();
    }

    @Bean
    @ConfigurationProperties("postprocess.datasource")
    public DataSourceProperties postprocessDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean("postprocess.datasource")
    @ConfigurationProperties("postprocess.datasource")
    public DataSource postprocessDataSource() {
        return sourceDataSourceProperties().initializeDataSourceBuilder()
                .type(HikariDataSource.class).build();
    }

//    @Bean(name = "postgresDataSource")
////	@Primary
//    public DataSource postgresDataSource() throws NamingException {
//        return DataSourceBuilder.create()
//                .url("jdbc:postgresql:sergey")
//                .driverClassName("org.postgresql.Driver")
//                .username("postgres")
//                .password("postgres")
//                .build();
//    }
}
