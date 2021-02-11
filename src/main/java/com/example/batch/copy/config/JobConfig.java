package com.example.batch.copy.config;

import com.example.batch.copy.service.JobCreatorService;
import org.springframework.batch.core.Job;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ ServiceConfig.class })
public class JobConfig {

	@Bean
	Job job(JobCreatorService copyService) {
		return copyService.createJob();
	}

}
