package webprogramming.csc1106.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webprogramming.csc1106.Entities.UploadCourse;
import webprogramming.csc1106.Repositories.UploadCourseRepository;

import java.util.List;

@Service
public class UploadCourseService {
    @Autowired
    private UploadCourseRepository courseRepository;

    public List<UploadCourse> getAllCourses() {
        return courseRepository.findAll();
    }

    public UploadCourse addCourse(UploadCourse course) {
        return courseRepository.save(course);
    }

    public long getTotalCourses() {
        return courseRepository.count();
    }
}
