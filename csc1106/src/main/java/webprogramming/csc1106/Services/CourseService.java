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

    // Get All Courses from Courses Table
    public List<Course> getAllCourses() {
        String sql = "SELECT * FROM Courses";
    
        RowMapper<Course> rowMapper = new RowMapper<Course>() {
            @Override
            public Course mapRow(ResultSet rs, int rowNum) throws SQLException {
                Course course = new Course();
                course.setCourseId(rs.getInt("courseId"));
                course.setCourseName(rs.getString("courseName"));
                course.setCourseDescription(rs.getString("courseDescription"));
                course.setCourseInstructor(rs.getString("courseInstructor"));
                course.setCourseLevel(rs.getString("courseLevel"));
                course.setCoursePrice(rs.getString("coursePrice"));
                course.setCourseDuration(rs.getString("courseDuration"));
                course.setCourseImageUrl(rs.getString("courseImageUrl"));
                // Set other Course properties here, based on your Course model
                return course;
            }
        };
    
        return jdbcTemplate.query(sql, rowMapper);
    }
    
    // Get Course by ID
    public Course getCourseById(int courseId) {
        String sql = "SELECT * FROM Courses WHERE courseId = ?";
    
        RowMapper<Course> rowMapper = new RowMapper<Course>() {
            @Override
            public Course mapRow(ResultSet rs, int rowNum) throws SQLException {
                Course course = new Course();
                course.setCourseId(rs.getInt("courseId"));
                course.setCourseName(rs.getString("courseName"));
                course.setCourseDescription(rs.getString("courseDescription"));
                course.setCourseInstructor(rs.getString("courseInstructor"));
                course.setCourseLevel(rs.getString("courseLevel"));
                course.setCoursePrice(rs.getString("coursePrice"));
                course.setCourseDuration(rs.getString("courseDuration"));
                course.setCourseImageUrl(rs.getString("courseImageUrl"));
                // Set other Course properties here, based on your Course model
                return course;
            }
        };
    
        return jdbcTemplate.queryForObject(sql, rowMapper, courseId);
    }

    // Add a new Course
    public void addCourse(Course course) {
        String sql = "INSERT INTO Courses (courseName, courseDescription, courseInstructor, courseLevel, coursePrice, courseDuration, courseImageUrl) VALUES (?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, course.getCourseName(), course.getCourseDescription(), course.getCourseInstructor(), course.getCourseLevel(), course.getCoursePrice(), course.getCourseDuration(), course.getCourseImageUrl());
    }

    // Update a Course
    public void updateCourse(Course course) {
        String sql = "UPDATE Courses SET courseName = ?, courseDescription = ?, courseInstructor = ?, courseLevel = ?, coursePrice = ?, courseDuration = ?, courseImageUrl = ? WHERE courseId = ?";
        jdbcTemplate.update(sql, course.getCourseName(), course.getCourseDescription(), course.getCourseInstructor(), course.getCourseLevel(), course.getCoursePrice(), course.getCourseDuration(), course.getCourseImageUrl(), course.getCourseId());
    }

    // Delete a Course
    public void deleteCourse(int courseId) {
        String sql = "DELETE FROM Courses WHERE courseId = ?";
        jdbcTemplate.update(sql, courseId);
    }

    // Get Courses by Instructor
    public List<Course> getCoursesByInstructor(String courseInstructor) {
        String sql = "SELECT * FROM Courses WHERE courseInstructor = ?";
    
        RowMapper<Course> rowMapper = new RowMapper<Course>() {
            @Override
            public Course mapRow(ResultSet rs, int rowNum) throws SQLException {
                Course course = new Course();
                course.setCourseId(rs.getInt("courseId"));
                course.setCourseName(rs.getString("courseName"));
                course.setCourseDescription(rs.getString("courseDescription"));
                course.setCourseInstructor(rs.getString("courseInstructor"));
                course.setCourseLevel(rs.getString("courseLevel"));
                course.setCoursePrice(rs.getString("coursePrice"));
                course.setCourseDuration(rs.getString("courseDuration"));
                course.setCourseImageUrl(rs.getString("courseImageUrl"));
                // Set other Course properties here, based on your Course model
                return course;
            }
        };
    
        return jdbcTemplate.query(sql, rowMapper, courseInstructor);
    }

    // Get Courses by Level
    public List<Course> getCoursesByLevel(String courseLevel) {
        String sql = "SELECT * FROM Courses WHERE courseLevel = ?";
    
        RowMapper<Course> rowMapper = new RowMapper<Course>() {
            @Override
            public Course mapRow(ResultSet rs, int rowNum) throws SQLException {
                Course course = new Course();
                course.setCourseId(rs.getInt("courseId"));
                course.setCourseName(rs.getString("courseName"));
                course.setCourseDescription(rs.getString("courseDescription"));
                course.setCourseInstructor(rs.getString("courseInstructor"));
                course.setCourseLevel(rs.getString("courseLevel"));
                course.setCoursePrice(rs.getString("coursePrice"));
                course.setCourseDuration(rs.getString("courseDuration"));
                course.setCourseImageUrl(rs.getString("courseImageUrl"));
                // Set other Course properties here, based on your Course model
                return course;
            }
        };
    
        return jdbcTemplate.query(sql, rowMapper, courseLevel);
    }

}
