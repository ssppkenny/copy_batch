package com.example.batch.copy.service;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.jdbc.datasource.init.ScriptUtils;

public class DefaultJobCreatorService implements JobCreatorService {

	private static Logger logger = LoggerFactory.getLogger(DefaultJobCreatorService.class);

	private DefaultJobCreatorService() {

	}

	private JobBuilderFactory jobBuilderFactory;

	private DataSource preprocessDataSource;

	private DataSource postprocessDataSource;

	private Environment environment;

	private StepCreator stepCreator;

	public static class Builder {

		private JobBuilderFactory jobBuilderFactory;

		private DataSource preprocessDataSource;

		private DataSource postprocessDataSource;

		private Environment environment;

		private StepCreator stepCreator;

		public Builder jobBuilder(JobBuilderFactory jobBuilderFactory) {
			this.jobBuilderFactory = jobBuilderFactory;
			return this;
		}

		public Builder environment(Environment environment) {
			this.environment = environment;
			return this;
		}

		public Builder postprocess(DataSource dataSource) {
			this.postprocessDataSource = dataSource;
			return this;
		}

		public Builder preprocess(DataSource dataSource) {
			this.preprocessDataSource = dataSource;
			return this;
		}

		public Builder stepCreator(StepCreator stepCreator) {
			this.stepCreator = stepCreator;
			return this;

		}

		public DefaultJobCreatorService build() {
			DefaultJobCreatorService copier = new DefaultJobCreatorService();
			copier.jobBuilderFactory = this.jobBuilderFactory;
			copier.environment = this.environment;
			copier.preprocessDataSource = this.preprocessDataSource;
			copier.postprocessDataSource = this.postprocessDataSource;
			copier.stepCreator = this.stepCreator;
			return copier;
		}
	}

	@Override
	public Job createJob() {

		Resource resource = new ClassPathResource("./copy-tables" + getActiveProfile() + ".sql", DefaultJobCreatorService.class.getClassLoader());

		List<StepDescription> stepDescriptions = stepCreator.getStepDescriptions(resource);
		List<Step> steps = stepCreator.getStepList(stepDescriptions);

		return this.jobBuilderFactory.get("basicJob").start(createParallelFlow(steps)).end().listener(new JobExecutionListener() {
			@Override
			public void beforeJob(JobExecution jobExecution) {
				try {
					logger.info("starting preprocess sql");
					executeScript("./preprocess" + getActiveProfile() + ".sql", preprocessDataSource);
					logger.info("finished preprocess sql");
				} catch (FileNotFoundException e) {
					// ignore if no preprocess file found
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public void afterJob(JobExecution jobExecution) {
				try {
					logger.info("starting postprocess sql");
					executeScript("./postprocess" + getActiveProfile() + ".sql", postprocessDataSource);
					logger.info("finished postprocess sql");
				} catch (FileNotFoundException e) {
					// ignore if no postprocess file found
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}).build();
	}

	private void executeScript(String path, DataSource dataSource) throws Exception {
		Resource resource = new ClassPathResource(path, DefaultJobCreatorService.class.getClassLoader());
		if (resource != null && resource.contentLength() > 0) {
			ScriptUtils.executeSqlScript(dataSource.getConnection(), resource);
		}
	}

	private Flow createParallelFlow(List<Step> steps) {
		SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
		taskExecutor.setConcurrencyLimit(steps.size());

		List<Flow> flows = steps.stream() // we have to convert the steps to a flows
				.map((Step step) -> //
				new FlowBuilder<Flow>("flow_" + step.getName()) //
						.start(step) //
						.build()) //
				.collect(Collectors.toList());

		return new FlowBuilder<SimpleFlow>("parallelStepsFlow").split(taskExecutor) //
				.add(flows.toArray(new Flow[flows.size()])) //
				.build();
	}

	private String getActiveProfile() {
		String[] activeProfiles = environment.getActiveProfiles();
		if (activeProfiles.length > 0) {
			String activeProfile = activeProfiles[0];
			return "-" + activeProfile;
		} else {
			return "";
		}

	}

}
