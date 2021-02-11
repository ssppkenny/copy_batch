package com.example.batch.copy.config;

import javax.sql.DataSource;

import com.example.batch.copy.service.DefaultJobCreatorService;
import com.example.batch.copy.service.JobCreatorService;
import com.example.batch.copy.service.LinesReader;
import com.example.batch.copy.service.StepCreator;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

@Configuration
@Import({ DataSourceConfig.class })
public class ServiceConfig {

	@Bean
    LinesReader linesReader() {
		return new LinesReader();
	}

	@Bean
    StepCreator stepCreator(StepBuilderFactory stepBuilderFactory,
                            @Qualifier(DataSourceConfig.SOURCE_DATASOURCE_NAME) DataSource sourceDataSource,
                            @Qualifier(DataSourceConfig.DESTINATION_DATASOURCE_NAME) DataSource destinationDataSource,
                            LinesReader linesReader) {
		return new StepCreator(stepBuilderFactory, sourceDataSource, destinationDataSource, linesReader);
	}

	@Bean
    JobCreatorService copyService(Environment environment,
                                  @Qualifier(DataSourceConfig.SOURCE_DATASOURCE_NAME) DataSource sourceDataSource,
                                  @Qualifier(DataSourceConfig.DESTINATION_DATASOURCE_NAME) DataSource destinationDataSource,
                                  @Qualifier(DataSourceConfig.PREPROCESS_DATASOURCE_NAME) DataSource preprocessDataSource,
                                  @Qualifier(DataSourceConfig.POSTPROCESS_DATASOURCE_NAME) DataSource postprocessDataSource,
                                  JobBuilderFactory jobBuilderFactory,
                                  StepBuilderFactory stepBuilderFactory,
                                  LinesReader linesReader

	) {
		return new DefaultJobCreatorService.Builder().jobBuilder(jobBuilderFactory)
				.environment(environment)
				.postprocess(postprocessDataSource)
				.preprocess(preprocessDataSource)
				.stepCreator(stepCreator(stepBuilderFactory, sourceDataSource, destinationDataSource, linesReader))
				.build();
	}

}
