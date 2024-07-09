package webprogramming.csc1106.Services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import webprogramming.csc1106.Entities.FileResource;
import webprogramming.csc1106.Entities.Lesson;
import webprogramming.csc1106.Entities.Section;
import webprogramming.csc1106.Repositories.LessonRepository;
import webprogramming.csc1106.Repositories.SectionRepository;
import webprogramming.csc1106.Repositories.FileResourceRepository;

@Service
public class LessonService {

     @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private FileResourceRepository fileResourceRepository;

    public void addLesson(Long sectionId, String title) {
        Section section = sectionRepository.findById(sectionId).orElseThrow(() -> new RuntimeException("Section not found"));
        Lesson lesson = new Lesson(title, section);
        lessonRepository.save(lesson);
    }

    public void deleteLesson(Long id) {
        lessonRepository.deleteById(id);
    }

    public Lesson updateLesson(Long id, Lesson updatedLesson) {
        Lesson existingLesson = getLessonById(id);
        existingLesson.setTitle(updatedLesson.getTitle());
        existingLesson.setFile(updatedLesson.getFile());
        return lessonRepository.save(existingLesson);
    }

    //get list of lesson by section id
    public List<Lesson> getLessonsBySectionId(Long sectionId) {
        return lessonRepository.findBySectionId(sectionId);
    }

    //create lesson by lesson model attributes and section id
    
    public Lesson createLesson(Lesson lesson, long sectionId) {
        Section section = sectionRepository.findById(sectionId);
        lesson.setSection(section);
        return lessonRepository.save(lesson);

    }

    //get lesson by lesson id
    public Lesson getLessonById(Long lessonId) {
        return lessonRepository.findById(lessonId).orElseThrow(() -> new RuntimeException("Lesson not found"));
    }


    // delete file based on file resource id
    public void deleteFile(Long fileId) {
        fileResourceRepository.deleteById(fileId);
    }

    //edit file resource with the updated file 
    public FileResource updateFile(Long fileId, FileResource updatedFile) {
        FileResource existingFile = getFileById(fileId);
        existingFile.setFileName(updatedFile.getFileName());
        existingFile.setFileUrl(updatedFile.getFileUrl());
        return fileResourceRepository.save(existingFile);
    }

    public FileResource getFileById(Long fileId) {
        //logging fileId
        System.out.println("File Id: " + fileId);
        return fileResourceRepository.findById(fileId).orElseThrow(() -> new RuntimeException("File not found"));
    }
    
}
