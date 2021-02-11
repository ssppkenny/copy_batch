package com.example.batch.copy;

import org.springframework.batch.item.database.JdbcCursorItemReader;

import java.util.Map;

public class CopyItemReader extends JdbcCursorItemReader<Map<Integer,Object>> {

    private String sql;

    public CopyItemReader(String sql) {
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return sql;
    }
}
