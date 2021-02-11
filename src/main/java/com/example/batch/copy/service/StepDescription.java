package com.example.batch.copy.service;

public class StepDescription {

	private final String tableName;

	private final int number;

	private final String selectSql;

	private final String insertSql;

	public StepDescription(String tableName, int number, String selectSql, String insertSql) {
		this.tableName = tableName;
		this.number = number;
		this.selectSql = selectSql;
		this.insertSql = insertSql;
	}

	public String getTableName() {
		return tableName;
	}

	public int getNumber() {
		return number;
	}

	public String getSelectSql() {
		return selectSql;
	}

	public String getInsertSql() {
		return insertSql;
	}

	@Override
	public String toString() {
		return "StepDescription [tableName=" + tableName + ", number=" + number + ", selectSql=" + selectSql + ", insertSql=" + insertSql + "]";
	}

}
