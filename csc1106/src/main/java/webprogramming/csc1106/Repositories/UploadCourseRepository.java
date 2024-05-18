package webprogramming.csc1106.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import webprogramming.csc1106.Entities.UploadCourse;

@Repository
public interface UploadCourseRepository extends JpaRepository<UploadCourse, Long> {
}
