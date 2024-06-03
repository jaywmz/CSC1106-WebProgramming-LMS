package webprogramming.csc1106.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobClientBuilder;
import webprogramming.csc1106.Entities.*;
import webprogramming.csc1106.Repositories.*;

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
    private CategoryGroupRepository categoryGroupRepository;

    @Autowired
    private CourseCategoryRepository courseCategoryRepository;

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
        if (course.getCourseCategories() == null) {
            course.setCourseCategories(new ArrayList<>());
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

    public CourseCategory addCourseCategory(CourseCategory courseCategory) {
        return courseCategoryRepository.save(courseCategory);
    }

    public List<CategoryGroup> getAllCategories() {
        return categoryGroupRepository.findAll();
    }

    public Optional<CategoryGroup> getCategoryById(Long id) {
        return categoryGroupRepository.findById(id);
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
        // Delete cover image blob
        if (course.getCoverImageUrl() != null) {
            azureBlobService.deleteBlob(course.getCoverImageUrl());
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
        // Here we keep the sections check to avoid null pointer exceptions
        if (course.getSections() == null) {
            course.setSections(new ArrayList<>());
        }
        if (course.getCourseCategories() == null) {
            course.setCourseCategories(new ArrayList<>());
        }
        return courseRepository.save(course);
    }

    public void deleteBlob(String blobUrl) {
        azureBlobService.deleteBlob(blobUrl);
    }
}
