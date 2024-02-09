package com.mediaportal.faces_api.application.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JobRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void RecordJobRepository(String job_id, Integer type, String date) {

        String queryString = String.format("INSERT INTO ia_request (job_id, type, date) \n" +
                "VALUES ('%s', %d, '%s');", job_id, type, date );

        System.out.println(queryString);

        jdbcTemplate.execute(queryString);
    }

}
