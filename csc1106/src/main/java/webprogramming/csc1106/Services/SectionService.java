package webprogramming.csc1106.Services;

import webprogramming.csc1106.Entities.Section;
import webprogramming.csc1106.Entities.UploadCourse;
import webprogramming.csc1106.Entities.UploadCourse;
import webprogramming.csc1106.Repositories.SectionRepository;
import webprogramming.csc1106.Repositories.UploadCourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import webprogramming.csc1106.Repositories.UserRepository;


@Service
public class SectionService {
    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private UploadCourseRepository courseRepository;

    public void addSection(Long courseId, String title, String description) {
        UploadCourse course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));
        Section section = new Section(title, description, course);
        sectionRepository.save(section);
    }

    //create section
    public void createSection(Section section, long courseId) {
        UploadCourse course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));
        section.setCourse(course);
        sectionRepository.save(section);
    }

    //get section by course id
    public List<Section> getSectionsByCourseId(Long courseId) {
        return sectionRepository.findByCourseId(courseId);
    }

    public Section getSectionById(Long id) {
        return sectionRepository.findById(id).orElseThrow(() -> new RuntimeException("Section not found"));
    }

    public void deleteSection(Long id) {
        sectionRepository.deleteById(id);
    }

    public Section updateSection(Long id, Section updatedSection) {
        Section existingSection = getSectionById(id);
        existingSection.setTitle(updatedSection.getTitle());
        existingSection.setDescription(updatedSection.getDescription());
        return sectionRepository.save(existingSection);
    }

}
