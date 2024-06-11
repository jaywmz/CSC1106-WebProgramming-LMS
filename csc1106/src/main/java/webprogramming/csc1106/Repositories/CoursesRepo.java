package webprogramming.csc1106.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import webprogramming.csc1106.Entities.CourseEntity;
import webprogramming.csc1106.Entities.CourseCategoriesEntity;

public interface CoursesRepo extends JpaRepository<CourseEntity, Integer>{

    @Query("SELECT c FROM CourseEntity c WHERE c.courseName = :courseName")
    CourseEntity findByCourseName(@Param("courseName") String courseName);
    
    @Query("SELECT c FROM CourseEntity c WHERE c.id = :courseId")
    CourseEntity findByCourseId(@Param("courseId") int courseId);

    @Query("SELECT c FROM CourseEntity c WHERE c.instructor = :instructor")
    List<CourseEntity> findByInstructor(@Param("instructor") String instructor);

    //Get All Courses
    @Query("SELECT c FROM CourseEntity c")
    List<CourseEntity> getAllCourses();
}
