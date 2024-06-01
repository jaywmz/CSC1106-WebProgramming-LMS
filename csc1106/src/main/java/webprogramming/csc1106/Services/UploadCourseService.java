package webprogramming.csc1106.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobClientBuilder;

import webprogramming.csc1106.Entities.FileResource;
import webprogramming.csc1106.Entities.Lesson;
import webprogramming.csc1106.Entities.Section;
import webprogramming.csc1106.Entities.UploadCourse;
import webprogramming.csc1106.Repositories.FileResourceRepository;
import webprogramming.csc1106.Repositories.LessonRepository;
import webprogramming.csc1106.Repositories.SectionRepository;
import webprogramming.csc1106.Repositories.UploadCourseRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UploadCourseService {

    @Autowired
    private UploadCourseRepository courseRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private FileResourceRepository fileResourceRepository;

    @Autowired
    private AzureBlobService azureBlobService;

    public List<UploadCourse> getAllCourses() {
        return courseRepository.findAll();
    }

    public Optional<UploadCourse> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    public long getTotalCourses() {
        return courseRepository.count();
    }

    public UploadCourse addCourse(UploadCourse course) {
        if (course.getSections() == null) {
            course.setSections(new ArrayList<>());
        }
        return courseRepository.save(course);
    }

    public Section addSection(Section section) {
        return sectionRepository.save(section);
    }

    public Lesson addLesson(Lesson lesson) {
        return lessonRepository.save(lesson);
    }

    public FileResource addFileResource(FileResource fileResource) {
        return fileResourceRepository.save(fileResource);
    }

    public void deleteCourse(Long courseId) {
        UploadCourse course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        for (Section section : course.getSections()) {
            for (Lesson lesson : section.getLessons()) {
                for (FileResource file : lesson.getFiles()) {
                    azureBlobService.deleteBlob(file.getFileUrl());
                }
            }
        }
        courseRepository.deleteById(courseId);
    }

    public String uploadToAzureBlob(InputStream fileInputStream, String fileName) throws IOException {
        return azureBlobService.uploadToAzureBlob(fileInputStream, fileName);
    }

    public String generateSasUrl(String blobUrl) {
        return azureBlobService.generateSasUrl(blobUrl);
    }

    public InputStream downloadFileWithSas(String blobUrl) {
        String sasUrl = azureBlobService.generateSasUrl(blobUrl);
        BlobClient blobClient = new BlobClientBuilder()
                .endpoint(sasUrl)
                .buildClient();
        return blobClient.openInputStream();
    }

    public Optional<FileResource> getFileResourceById(Long id) {
        return fileResourceRepository.findById(id);
    }

    public UploadCourse addCourseWithFile(UploadCourse course, InputStream fileInputStream, String fileName) throws IOException {
        String blobUrl = uploadToAzureBlob(fileInputStream, fileName);
        blobUrl = generateSasUrl(blobUrl);
        FileResource fileResource = new FileResource(fileName, blobUrl);
        fileResource.setLesson(null);  // Ensure this does not break your business logic
        fileResourceRepository.save(fileResource);
        return addCourse(course);
    }

    public UploadCourse updateCourse(UploadCourse course) {
        if (course.getSections() == null) {
            course.setSections(new ArrayList<>());
        }
        return courseRepository.save(course);
    }
}
