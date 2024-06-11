package webprogramming.csc1106.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.RowMapper;
import webprogramming.csc1106.Models.Course;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class CourseService {
    private final JdbcTemplate jdbcTemplate;
    
    @Autowired
    public CourseService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

}
