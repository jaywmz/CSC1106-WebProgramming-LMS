package webprogramming.csc1106.Services;

import com.azure.storage.blob.BlobClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webprogramming.csc1106.Config.AzureStorageProperties;
import webprogramming.csc1106.Entities.UploadCourse;
import webprogramming.csc1106.Repositories.UploadCourseRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class UploadCourseService {
    @Autowired
    private UploadCourseRepository courseRepository;

    @Autowired
    private AzureStorageProperties azureStorageProperties;

    public List<UploadCourse> getAllCourses() {
        return courseRepository.findAll();
    }

    public UploadCourse addCourse(UploadCourse course, InputStream fileInputStream, String fileName) throws IOException {
        // Upload file to Azure Blob Storage
        String blobUrl = uploadToAzureBlob(fileInputStream, fileName);
        course.setBlobUrl(blobUrl);
        course.setBlobName(fileName);

        // Save course metadata to MySQL
        return courseRepository.save(course);
    }

    public long getTotalCourses() {
        return courseRepository.count();
    }

    private String uploadToAzureBlob(InputStream fileInputStream, String fileName) throws IOException {
        String connectionString = azureStorageProperties.getConnectionString();
        String containerName = azureStorageProperties.getContainerName();

        new BlobClientBuilder()
            .connectionString(connectionString)
            .containerName(containerName)
            .blobName(fileName)
            .buildClient()
            .upload(fileInputStream, fileInputStream.available(), true);

        return String.format("https://%s.blob.core.windows.net/%s/%s", connectionString.split(";")[1].split("=")[1], containerName, fileName);
    }
}