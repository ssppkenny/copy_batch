package com.example.batch.copy;

import com.example.batch.copy.config.JobConfig;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Import;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@Import({ JobConfig.class })
@EnableBatchProcessing
public class CopyApplication {

	// spring batch starts all job beans found in the context
	// the job is configured in the JobConfig

	public static void main(String[] args) {
		SpringApplication.run(CopyApplication.class, args);
	}

}
