package com.example.batch.copy.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

import com.example.batch.copy.sql.CopyTableParameterizedPreparedStatementSetter;
import com.example.batch.copy.sql.CopyTableRowMapper;

public class StepCreator {

	Pattern INSERT_SQL_PATTERN = Pattern.compile("INSERT\\s+INTO\\s+(\\w+)\\s+.*", Pattern.CASE_INSENSITIVE);

	private static final Logger logger = LoggerFactory.getLogger(StepCreator.class);

	private final StepBuilderFactory stepBuilderFactory;

	private final DataSource destinationDataSource;

	private final DataSource sourceDataSource;

	private final LinesReader linesReader;

	public StepCreator(StepBuilderFactory stepBuilderFactory, DataSource sourceDataSource, DataSource destinationDataSource, LinesReader linesReader) {
		this.stepBuilderFactory = stepBuilderFactory;
		this.destinationDataSource = destinationDataSource;
		this.sourceDataSource = sourceDataSource;
		this.linesReader = linesReader;

	}

	public Step createStep(String tableName, int stepNumber,
			String selectSql, String insertSql) {
		CopyTableRowMapper rowMapper = new CopyTableRowMapper();
		CopyTableParameterizedPreparedStatementSetter setter = new CopyTableParameterizedPreparedStatementSetter(rowMapper);
		return stepBuilderFactory.get("copyStep" + "TABLE" + stepNumber + "_" + tableName)
				.allowStartIfComplete(true)
				.<Map<Integer, Object>, Map<Integer, Object>> chunk(10000)
				.reader(createReader(selectSql, rowMapper))
				.writer(createWriter(insertSql, setter))
				.listener(new StepExecutionListener() {

					@Override
					public void beforeStep(StepExecution stepExecution) {
						clearTable(insertSql, destinationDataSource);
						logger.info("cleared table " + tableName);
						logger.info("starting to copy table " + tableName);
					}

					@Override
					public ExitStatus afterStep(StepExecution stepExecution) {
						logger.info("copied table " + tableName);
						return stepExecution.getExitStatus();
					}
				})
				.build();
	}

	public List<StepDescription> getStepDescriptions(Resource resource) {
		List<StepDescription> steps = new ArrayList<>();
		try {
			List<String> lines = linesReader.readLines(resource);
			for (int i = 0; i < lines.size(); i++) {
				String line = lines.get(i);
				String[] parts = line.split(":");
				String insertSQL = parts[1];
				String selectSQL = parts[0];

				Matcher m = INSERT_SQL_PATTERN.matcher(insertSQL);
				if (m.matches()) {
					String tableName = m.group(1);
					steps.add(new StepDescription(tableName, i, selectSQL, insertSQL));
				}

			}
			logger.info("found " + steps.size() + " tables to copy");
			return steps;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public List<Step> getStepList(List<StepDescription> steps) {
		return steps.stream()
				.map(
						step -> createStep(
								step.getTableName(),
								step.getNumber(),
								step.getSelectSql(),
								step.getInsertSql()))
				.collect(Collectors.toList());
	}

	public List<String> readLines(Resource resource) throws IOException {
		return linesReader.readLines(resource);
	}

	private JdbcCursorItemReader<Map<Integer, Object>> createReader(String sql, CopyTableRowMapper rowMapper) {
		try {
			JdbcCursorItemReader<Map<Integer, Object>> reader = new JdbcCursorItemReader<>();
			reader.setSql(sql);
			reader.setDataSource(sourceDataSource);
			reader.setRowMapper(rowMapper);
			reader.afterPropertiesSet();
			return reader;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	private JdbcBatchItemWriter<Map<Integer, Object>> createWriter(String sql, CopyTableParameterizedPreparedStatementSetter setter) {
		JdbcBatchItemWriter<Map<Integer, Object>> writer = new JdbcBatchItemWriter<>();
		writer.setDataSource(destinationDataSource);
		writer.setSql(sql);
		writer.setItemPreparedStatementSetter(setter);
		writer.afterPropertiesSet();
		return writer;
	}

	private void clearTable(String insertSQL, DataSource dataSource) {
		Matcher m = INSERT_SQL_PATTERN.matcher(insertSQL);
		if (m.matches()) {
			String tableName = m.group(1);
			String sqlDelete = String.format("DELETE FROM %s", tableName);
			JdbcTemplate template = new JdbcTemplate(dataSource);
			template.update(sqlDelete);
			logger.info("finished " + sqlDelete);
		}
	}

}
