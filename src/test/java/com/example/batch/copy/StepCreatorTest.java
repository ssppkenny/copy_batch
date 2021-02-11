package com.example.batch.copy;

import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import com.example.batch.copy.service.LinesReader;
import com.example.batch.copy.service.StepCreator;
import com.example.batch.copy.service.StepDescription;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.core.io.Resource;

public class StepCreatorTest {

	@Mock
	private final StepBuilderFactory stepBuilderFactory = Mockito.mock(StepBuilderFactory.class);

	@Mock
	private final DataSource destinationDataSource = Mockito.mock(DataSource.class);

	@Mock
	private final DataSource sourceDataSource = Mockito.mock(DataSource.class);

	@Mock
	private final LinesReader linesReader = Mockito.mock(LinesReader.class);

	@Mock
	private final Resource resource = Mockito.mock(Resource.class);

	@Test
	public void testCreateStepList() throws Exception {

		List<String> lines = Arrays.asList(
				"SELECT pk, name FROM T1:INSERT INTO T2 VALUES (?, ?)",
				"SELECT pk, name FROM T3:INSERT INTO T4 VALUES (?, ?)");

		StepCreator stepCreator = new StepCreator(stepBuilderFactory, sourceDataSource, destinationDataSource, linesReader);
		Mockito.when(linesReader.readLines(Mockito.any(Resource.class))).thenReturn(lines);
		List<StepDescription> stepDescriptions = stepCreator.getStepDescriptions(resource);

		Assert.assertTrue(stepDescriptions.size() == 2);

		Assert.assertEquals(stepDescriptions.get(0).getTableName(), "T2");
		Assert.assertEquals(stepDescriptions.get(1).getTableName(), "T4");

	}

}
