package com.example.batch.copy;

import org.springframework.batch.item.database.ItemPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class CopyTableParameterizedPreparedStatementSetter implements ItemPreparedStatementSetter<Map<Integer, Object>> {

    private final CopyTableRowMapper rowMapper;

    public CopyTableParameterizedPreparedStatementSetter(CopyTableRowMapper rowMapper) {
        this.rowMapper = rowMapper;
    }


    @Override
    public void setValues(Map<Integer, Object> rowMap, PreparedStatement ps) throws SQLException {
        for (int i = 1; i <= rowMapper.getColumnCount(); i++) {
            ps.setObject(i, rowMap.get(i));
        }
    }
}
