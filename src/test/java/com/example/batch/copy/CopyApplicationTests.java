package com.example.batch.copy;

import javax.sql.DataSource;

import com.example.batch.copy.config.DataSourceConfig;
import com.example.batch.copy.config.JobConfig;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@ActiveProfiles("test")
@SpringBatchTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { BatchConfiguration.class, JobConfig.class })
class CopyApplicationTests {

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	@Qualifier(com.example.batch.copy.config.DataSourceConfig.SOURCE_DATASOURCE_NAME)
	DataSource sourceDataSource;

	@Autowired
	@Qualifier(DataSourceConfig.DESTINATION_DATASOURCE_NAME)
	DataSource destinationDataSource;

	@Autowired
	DataSource dataSource;

	@Test
	public void testExecutionStatus() throws Exception {

		ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("./schema-all.sql"));
		ScriptUtils.executeSqlScript(sourceDataSource.getConnection(), new ClassPathResource("db/populate-source.sql"));
		ScriptUtils.executeSqlScript(destinationDataSource.getConnection(), new ClassPathResource("db/populate-dest.sql"));
		JobExecution launchJob = jobLauncherTestUtils.launchJob();
		launchJob.getStepExecutions().forEach(stepExecution -> {
			Assert.assertEquals(3, stepExecution.getReadCount());
			Assert.assertEquals(3, stepExecution.getWriteCount());
			Assert.assertEquals(ExitStatus.COMPLETED, stepExecution.getExitStatus());
		});
		Assert.assertEquals(ExitStatus.COMPLETED, launchJob.getExitStatus());

	}

}
