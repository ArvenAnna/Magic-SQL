package com.anna.magic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class MagicController {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @RequestMapping(value = {"/sql_exec.req"}, method = RequestMethod.POST,
            headers = "Accept=application/json")
    @ResponseBody
    public List<String> executeSql(@RequestBody String sql) {
        List<String> list = new ArrayList<>();
        try {
            jdbcTemplate.execute(sql);
        } catch (Exception e) {
            list.add("error");
            list.add(e.getMessage());
            return list;
        }
        return list;
    }

    @RequestMapping(value = {"/sql_select.req"}, method = RequestMethod.POST,
            headers = "Accept=application/json")
    @ResponseBody
    public List<List<List<Object>>> executeSelect(@RequestBody String sql) {
        List<List<List<Object>>> list = new ArrayList<>();
        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> mapRow(rs));
        } catch (Exception e) {
            List<Object> error = new ArrayList<>();
            error.add("error");
            error.add(e.getMessage());
            List<List<Object>> errC = new ArrayList<>();
            errC.add(error);
            list.add(errC);
            return list;
        }
    }

    private List<List<Object>> mapRow(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        List<List<Object>> row = new ArrayList<>();
        for (int i = 1; i <= columnCount; i++) {
            String column = metaData.getColumnLabel(i);
            Object obj = rs.getObject(i);
            List<Object> list = new ArrayList<>();
            list.add(column);
            list.add(obj);
            row.add(list);
        }
        return row;
    }
}