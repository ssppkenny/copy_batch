package com.example.batch.copy.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;

public class CopyTableRowMapper implements RowMapper<Map<Integer, Object>> {

	private int columnCount;

	@Override
	public Map<Integer, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
		Map<Integer, Object> rowMap = new HashMap<>();
		columnCount = rs.getMetaData().getColumnCount();
		for (int i = 1; i <= columnCount; i++) {
			final Object value = rs.getObject(i);
			rowMap.put(i, value);
		}
		return rowMap;
	}

	public int getColumnCount() {
		return columnCount;
	}

}
