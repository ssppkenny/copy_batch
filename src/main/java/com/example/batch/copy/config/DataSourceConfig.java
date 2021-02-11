package com.example.batch.copy.config;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSourceConfig {

	public static final String POSTPROCESS_DATASOURCE_NAME = "postprocess.datasource";

	public static final String PREPROCESS_DATASOURCE_NAME = "preprocess.datasource";

	public static final String DESTINATION_DATASOURCE_NAME = "destination.datasource";

	public static final String SOURCE_DATASOURCE_NAME = "source.datasource";

	@Bean(name = "h2EmbeddedDataSource")
	@Primary
	DataSource h2EmbeddedDataSource() {
		EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
		return builder
				.setType(EmbeddedDatabaseType.H2)
				.build();
	}

	@Bean
	@ConfigurationProperties(SOURCE_DATASOURCE_NAME)
	DataSourceProperties sourceDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean(SOURCE_DATASOURCE_NAME)
	@ConfigurationProperties("source.datasource.configuration")
	DataSource sourceDataSource() {
		return sourceDataSourceProperties().initializeDataSourceBuilder()
				.type(HikariDataSource.class)
				.build();
	}

	@Bean
	@ConfigurationProperties(DESTINATION_DATASOURCE_NAME)
	DataSourceProperties destinationDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean(DESTINATION_DATASOURCE_NAME)
	@ConfigurationProperties("destination.datasource.configuration")
	DataSource destinationDataSource() {
		return destinationDataSourceProperties().initializeDataSourceBuilder()
				.type(HikariDataSource.class)
				.build();
	}

	@Bean
	@ConfigurationProperties(PREPROCESS_DATASOURCE_NAME)
	DataSourceProperties preprocessDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean(PREPROCESS_DATASOURCE_NAME)
	@ConfigurationProperties("preprocess.datasource.configuration")
	DataSource preprocessDataSource() {
		return preprocessDataSourceProperties().initializeDataSourceBuilder()
				.type(HikariDataSource.class)
				.build();
	}

	@Bean
	@ConfigurationProperties(POSTPROCESS_DATASOURCE_NAME)
	DataSourceProperties postprocessDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean(POSTPROCESS_DATASOURCE_NAME)
	@ConfigurationProperties("postprocess.datasource.configuration")
	DataSource postprocessDataSource() {
		return postprocessDataSourceProperties().initializeDataSourceBuilder()
				.type(HikariDataSource.class)
				.build();
	}

}
