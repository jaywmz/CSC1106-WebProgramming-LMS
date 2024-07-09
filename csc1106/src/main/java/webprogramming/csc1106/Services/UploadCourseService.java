package webprogramming.csc1106.Services;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobClientBuilder;

import webprogramming.csc1106.Entities.CategoryGroup;
import webprogramming.csc1106.Entities.CourseCategory;
import webprogramming.csc1106.Entities.FileResource;
import webprogramming.csc1106.Entities.Lesson;
import webprogramming.csc1106.Entities.PartnerCertificate;
import webprogramming.csc1106.Entities.PartnerPublish;
import webprogramming.csc1106.Entities.Rating;
import webprogramming.csc1106.Entities.Section;
import webprogramming.csc1106.Entities.UploadCourse;
import webprogramming.csc1106.Repositories.CategoryGroupRepository;
import webprogramming.csc1106.Repositories.CourseCategoryRepository;
import webprogramming.csc1106.Repositories.FileResourceRepository;
import webprogramming.csc1106.Repositories.LessonRepository;
import webprogramming.csc1106.Repositories.PartnerCertificateRepository;
import webprogramming.csc1106.Repositories.PartnerPublishRepository;
import webprogramming.csc1106.Repositories.RatingRepository;
import webprogramming.csc1106.Repositories.SectionRepository;
import webprogramming.csc1106.Repositories.UploadCourseRepository;


@Service
public class UploadCourseService {

    private static final Logger logger = LoggerFactory.getLogger(UploadCourseService.class);

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
    private PartnerCertificateRepository partnerCertificateRepository;

    @Autowired
    private PartnerPublishRepository partnerPublishRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private AzureBlobService azureBlobService;

    public List<UploadCourse> getAllCourses() {
        List<UploadCourse> courses = courseRepository.findAll();
        courses.forEach(this::calculateRating);
        return courses;
    }

    // get all course but filter out those not approved
    public List<UploadCourse> getAllApprovedCourses() {
        List<UploadCourse> courses = courseRepository.findByIsApprovedTrue();
        courses.forEach(this::calculateRating);
        return courses;
    }

    public Optional<UploadCourse> getCourseById(Long id) {
        Optional<UploadCourse> course = courseRepository.findById(id);
        course.ifPresent(this::calculateRating);
        return course;
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
        logger.info("Fetching category by id: {}", id);
        return categoryGroupRepository.findById(id);
    }

    public void clearCourseCategories(UploadCourse course) {
        List<CourseCategory> courseCategories = courseCategoryRepository.findByCourse(course);
        courseCategoryRepository.deleteAll(courseCategories);
        course.getCourseCategories().clear();
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

    public void processCourseUpload(UploadCourse course, MultipartFile coverImage, Long selectedCategory) throws IOException {
        if (coverImage != null && !coverImage.isEmpty()) {
            String contentType = coverImage.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new RuntimeException("Invalid file type for cover image. Only images are allowed.");
            }
            String coverImageUrl = uploadToAzureBlob(coverImage.getInputStream(), coverImage.getOriginalFilename());
            coverImageUrl = generateSasUrl(coverImageUrl);
            course.setCoverImageUrl(coverImageUrl);
        }

        addCourse(course);

        CategoryGroup categoryGroup = getCategoryById(selectedCategory)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        CourseCategory courseCategory = new CourseCategory(course, categoryGroup);
        addCourseCategory(courseCategory);
        course.getCourseCategories().add(courseCategory);

        for (Section section : course.getSections()) {
            section.setCourse(course);
            addSection(section);

            for (Lesson lesson : section.getLessons()) {
                lesson.setSection(section);
                addLesson(lesson);

                MultipartFile lessonFile = lesson.getFile();
                if (lessonFile != null && !lessonFile.isEmpty()) {
                    String fileUrl = uploadToAzureBlob(lessonFile.getInputStream(), lessonFile.getOriginalFilename());
                    fileUrl = generateSasUrl(fileUrl);
                    FileResource fileResource = new FileResource(lessonFile.getOriginalFilename(), fileUrl);
                    fileResource.setLesson(lesson);
                    addFileResource(fileResource);
                    lesson.getFiles().add(fileResource);
                }
            }
        }
    }

    public List<UploadCourse> getPendingCourses() {
        return courseRepository.findByIsApprovedFalse();
    }

    public List<UploadCourse> getApprovedCourses() {
        return courseRepository.findByIsApprovedTrue();
    }

    public List<UploadCourse> getApprovedCoursesByCategoryId(Long categoryId) {
        return courseRepository.findByCourseCategories_CategoryGroup_IdAndIsApprovedTrue(categoryId);
    }

    public void processCourseUpdate(UploadCourse course, MultipartFile coverImage, Long selectedCategory) throws IOException {
        Optional<UploadCourse> existingCourseOpt = getCourseById(course.getId());
        if (!existingCourseOpt.isPresent()) {
            throw new RuntimeException("Course not found.");
        }

        UploadCourse existingCourse = existingCourseOpt.get();

        // Update cover image if a new one is provided
        if (coverImage != null && !coverImage.isEmpty()) {
            String contentType = coverImage.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new RuntimeException("Invalid file type for cover image. Only images are allowed.");
            }
            if (existingCourse.getCoverImageUrl() != null) {
                deleteBlob(existingCourse.getCoverImageUrl());
            }
            String coverImageUrl = uploadToAzureBlob(coverImage.getInputStream(), coverImage.getOriginalFilename());
            coverImageUrl = generateSasUrl(coverImageUrl);
            existingCourse.setCoverImageUrl(coverImageUrl);
        }

        // Update basic fields
        existingCourse.setTitle(course.getTitle());
        existingCourse.setDescription(course.getDescription());
        existingCourse.setLecturer(course.getLecturer());
        existingCourse.setPrice(course.getPrice());

        // Update the category
        clearCourseCategories(existingCourse);
        CategoryGroup categoryGroup = getCategoryById(selectedCategory)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        CourseCategory courseCategory = new CourseCategory(existingCourse, categoryGroup);
        addCourseCategory(courseCategory);
        existingCourse.getCourseCategories().add(courseCategory);

        // Clear and update sections and lessons
        existingCourse.getSections().clear();
        for (Section section : course.getSections()) {
            section.setCourse(existingCourse);
            Section savedSection = addSection(section);

            for (Lesson lesson : section.getLessons()) {
                lesson.setSection(savedSection);
                Lesson savedLesson = addLesson(lesson);

                MultipartFile lessonFile = lesson.getFile();
                if (lessonFile != null && !lessonFile.isEmpty()) {
                    for (FileResource existingFile : lesson.getFiles()) {
                        deleteBlob(existingFile.getFileUrl());
                    }
                    lesson.getFiles().clear();
                    String fileUrl = uploadToAzureBlob(lessonFile.getInputStream(), lessonFile.getOriginalFilename());
                    fileUrl = generateSasUrl(fileUrl);
                    FileResource fileResource = new FileResource(lessonFile.getOriginalFilename(), fileUrl);
                    fileResource.setLesson(savedLesson);
                    addFileResource(fileResource);
                    savedLesson.getFiles().add(fileResource);
                }
            }
            existingCourse.getSections().add(savedSection);
        }

        updateCourse(existingCourse);
    }

    public List<UploadCourse> getCoursesByCategoryId(Long categoryId) {
        List<CourseCategory> courseCategories = courseCategoryRepository.findByCategoryGroupId(categoryId);
        List<UploadCourse> courses = new ArrayList<>();
        for (CourseCategory courseCategory : courseCategories) {
            UploadCourse course = courseCategory.getCourse();
            calculateRating(course); // Calculate rating for each course
            courses.add(course);
        }
        return courses;
    }
    public List<UploadCourse> getFilteredAndSortedCourses(Long categoryId, String sortBy) {
        Sort sort = Sort.by(Sort.Direction.DESC, "price"); // Default sort by price descending

        if ("price-low-high".equals(sortBy)) {
            sort = Sort.by(Sort.Direction.ASC, "price");
        } else if ("price-high-low".equals(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "price");
        } else if ("rating".equals(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "averageRating");
        }

        List<UploadCourse> courses = courseRepository.findByCourseCategories_CategoryGroup_Id(categoryId, sort);
        return courses;
    }
    

    

    private void calculateRating(UploadCourse course) {
        List<Rating> ratings = ratingRepository.findByCourse(course);
        if (ratings != null && !ratings.isEmpty()) {
            double averageRating = ratings.stream().mapToDouble(Rating::getScore).average().orElse(0.0);
            int reviewCount = ratings.size();
            course.setAverageRating(averageRating);
            course.setReviewCount(reviewCount);
        } else {
            course.setAverageRating(0.0);
            course.setReviewCount(0);
        }
    }

    public UploadCourse partnerprocessCourseUpload(UploadCourse course, MultipartFile coverImage, Long selectedCategory) throws IOException {
        if (coverImage != null && !coverImage.isEmpty()) {
            String contentType = coverImage.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new RuntimeException("Invalid file type for cover image. Only images are allowed.");
            }
            String coverImageUrl = uploadToAzureBlob(coverImage.getInputStream(), coverImage.getOriginalFilename());
            coverImageUrl = generateSasUrl(coverImageUrl);
            course.setCoverImageUrl(coverImageUrl);
        }

        UploadCourse savedCourse = addCourse(course);

        CategoryGroup categoryGroup = getCategoryById(selectedCategory)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        CourseCategory courseCategory = new CourseCategory(course, categoryGroup);
        addCourseCategory(courseCategory);
        course.getCourseCategories().add(courseCategory);

        for (Section section : course.getSections()) {
            section.setCourse(course);
            addSection(section);

            for (Lesson lesson : section.getLessons()) {
                lesson.setSection(section);
                addLesson(lesson);

                MultipartFile lessonFile = lesson.getFile();
                if (lessonFile != null && !lessonFile.isEmpty()) {
                    String fileUrl = uploadToAzureBlob(lessonFile.getInputStream(), lessonFile.getOriginalFilename());
                    fileUrl = generateSasUrl(fileUrl);
                    FileResource fileResource = new FileResource(lessonFile.getOriginalFilename(), fileUrl);
                    fileResource.setLesson(lesson);
                    addFileResource(fileResource);
                    lesson.getFiles().add(fileResource);
                }
            }
        }
        return savedCourse; 
    }

    public void updateCourse(UploadCourse course, MultipartFile coverImage, Long categoryId,String certificateBlobUrl, String certificateTitle) throws IOException {
        Optional<UploadCourse> existingCourseOpt = getCourseById(course.getId());
        if (!existingCourseOpt.isPresent()) {
            throw new RuntimeException("Course not found.");
        }

        UploadCourse existingCourse = existingCourseOpt.get();

        // Update cover image if a new one is provided
        if (coverImage != null && !coverImage.isEmpty()) {
            String contentType = coverImage.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new RuntimeException("Invalid file type for cover image. Only images are allowed.");
            }
            if (existingCourse.getCoverImageUrl() != null) {
                deleteBlob(existingCourse.getCoverImageUrl());
            }
            String coverImageUrl = uploadToAzureBlob(coverImage.getInputStream(), coverImage.getOriginalFilename());
            coverImageUrl = generateSasUrl(coverImageUrl);
            existingCourse.setCoverImageUrl(coverImageUrl);
        }

        // Update basic fields
        existingCourse.setTitle(course.getTitle());
        existingCourse.setDescription(course.getDescription());
        existingCourse.setLecturer(course.getLecturer());
        existingCourse.setPrice(course.getPrice());

        // Update the category
        clearCourseCategories(existingCourse);
        CategoryGroup categoryGroup = getCategoryById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        CourseCategory courseCategory = new CourseCategory(existingCourse, categoryGroup);
        addCourseCategory(courseCategory);
        existingCourse.getCourseCategories().add(courseCategory);

    // Update the certificate details
    if (certificateBlobUrl != null && certificateTitle != null) {
        PartnerPublish partnerpublish = partnerPublishRepository.findByCourse(existingCourse);
        PartnerCertificate certificate = partnerCertificateRepository.findByPartnerPublish(partnerpublish);
        certificate.setCertificateName(certificateTitle);
        certificate.setBlobUrl(certificateBlobUrl); // Set Blob URL
        certificate.setIssueDate(LocalDateTime.now());

        // Save the updated certificate details
        addPartnerCertificate(certificate);

        // Update the PartnerPublish entity with the new certificate details
        PartnerPublish publish = partnerPublishRepository.findByCourse(existingCourse);
        publish.setCertificate(certificate);

        // Save the updated PartnerPublish entity
        addPartnerPublish(publish);
    }

    updateCourse(existingCourse);
}


    public PartnerCertificate addPartnerCertificate(PartnerCertificate certificate) {
        return partnerCertificateRepository.save(certificate);
    }

    public PartnerPublish addPartnerPublish(PartnerPublish publish) {
        return partnerPublishRepository.save(publish);
    }

    public List<UploadCourse> getCoursesByPartnerId(Integer partnerId) {
        List<PartnerPublish> publishes = partnerPublishRepository.findByPartnerPartnerId(partnerId);
        return publishes.stream()
                        .map(PartnerPublish::getCourse)
                        .collect(Collectors.toList());
    }

     // Method to get PartnerPublish by partnerId
     public List<PartnerPublish> getPartnerPublishByPartnerId(Integer partnerId) {
        return partnerPublishRepository.findByPartnerPartnerId(partnerId);
    }

     public List<CourseCategory> getCourseCategoriesByCourseIds(List<Long> courseIds) {
        return courseCategoryRepository.findByCourseIdIn(courseIds);
    }
}
