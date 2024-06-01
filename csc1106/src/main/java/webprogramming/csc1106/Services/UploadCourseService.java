package webprogramming.csc1106.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.options.BlockBlobOutputStreamOptions;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.azure.storage.common.sas.SasProtocol;

import webprogramming.csc1106.Config.AzureStorageProperties;
import webprogramming.csc1106.Entities.Lesson;
import webprogramming.csc1106.Entities.Section;
import webprogramming.csc1106.Entities.UploadCourse;
import webprogramming.csc1106.Repositories.LessonRepository;
import webprogramming.csc1106.Repositories.SectionRepository;
import webprogramming.csc1106.Repositories.UploadCourseRepository;

import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class UploadCourseService {
    @Autowired
    private UploadCourseRepository courseRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private AzureStorageProperties azureStorageProperties;

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

    public UploadCourse addCourseWithFile(UploadCourse course, InputStream fileInputStream, String fileName) throws IOException {
        String blobUrl = uploadToAzureBlob(fileInputStream, fileName);
        course.setBlobUrl(blobUrl);
        course.setBlobName(fileName);
        return addCourse(course);
    }

    public Section addSection(Section section) {
        return sectionRepository.save(section);
    }

    public Lesson addLesson(Lesson lesson, InputStream fileInputStream, String fileName) throws IOException {
        String blobUrl = uploadToAzureBlob(fileInputStream, fileName);
        lesson.setFileUrl(blobUrl);
        lesson.setFileName(fileName);
        return lessonRepository.save(lesson);
    }

    public UploadCourse updateCourse(UploadCourse course) {
        if (course.getSections() == null) {
            course.setSections(new ArrayList<>());
        }
        return courseRepository.save(course);
    }

    public void deleteCourse(Long courseId) {
        UploadCourse course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        deleteFromAzureBlob(course.getBlobName());
        courseRepository.deleteById(courseId);
    }

    @Async
    public CompletableFuture<Void> deleteCourseAsync(Long courseId) {
        deleteCourse(courseId);
        return CompletableFuture.completedFuture(null);
    }

    private void deleteFromAzureBlob(String blobName) {
        String connectionString = azureStorageProperties.getConnectionString();
        String containerName = azureStorageProperties.getContainerName();

        new BlobClientBuilder()
                .connectionString(connectionString)
                .containerName(containerName)
                .blobName(blobName)
                .buildClient()
                .delete();
    }

    private String uploadToAzureBlob(InputStream fileInputStream, String fileName) throws IOException {
        String connectionString = azureStorageProperties.getConnectionString();
        String containerName = azureStorageProperties.getContainerName();

        BlobClient blobClient = new BlobClientBuilder()
                .connectionString(connectionString)
                .containerName(containerName)
                .blobName(fileName)
                .buildClient();

        BlockBlobOutputStreamOptions options = new BlockBlobOutputStreamOptions()
                .setHeaders(new BlobHttpHeaders().setContentType("application/octet-stream"));

        try (var blobOutputStream = blobClient.getBlockBlobClient().getBlobOutputStream(options)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                blobOutputStream.write(buffer, 0, bytesRead);
            }
        }

        return blobClient.getBlobUrl();
    }

    public InputStream downloadFileWithSas(String blobName) {
        String connectionString = azureStorageProperties.getConnectionString();
        String containerName = azureStorageProperties.getContainerName();

        BlobClient blobClient = new BlobClientBuilder()
                .connectionString(connectionString)
                .containerName(containerName)
                .blobName(blobName)
                .buildClient();

        OffsetDateTime expiryTime = OffsetDateTime.now().plusMinutes(1);
        BlobSasPermission permissions = new BlobSasPermission().setReadPermission(true);
        BlobServiceSasSignatureValues sasValues = new BlobServiceSasSignatureValues(expiryTime, permissions)
                .setProtocol(SasProtocol.HTTPS_ONLY);

        String sasToken = blobClient.generateSas(sasValues);
        String sasUrl = blobClient.getBlobUrl() + "?" + sasToken;

        BlobClient sasBlobClient = new BlobClientBuilder()
                .endpoint(sasUrl)
                .buildClient();

        return sasBlobClient.openInputStream();
    }

    public String generateSasUrl(String blobName) {
        String connectionString = azureStorageProperties.getConnectionString();
        String containerName = azureStorageProperties.getContainerName();

        BlobClient blobClient = new BlobClientBuilder()
                .connectionString(connectionString)
                .containerName(containerName)
                .blobName(blobName)
                .buildClient();

        OffsetDateTime expiryTime = OffsetDateTime.now().plusHours(1);
        BlobSasPermission permissions = new BlobSasPermission().setReadPermission(true);
        BlobServiceSasSignatureValues sasValues = new BlobServiceSasSignatureValues(expiryTime, permissions)
                .setProtocol(SasProtocol.HTTPS_ONLY);

        return blobClient.getBlobUrl() + "?" + blobClient.generateSas(sasValues);
    }
}
