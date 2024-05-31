package webprogramming.csc1106.Services;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobClientBuilder;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.azure.storage.common.sas.SasProtocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webprogramming.csc1106.Config.AzureStorageProperties;
import webprogramming.csc1106.Entities.UploadCourse;
import webprogramming.csc1106.Repositories.UploadCourseRepository;

import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;
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

    public UploadCourse getCourseById(Long id) {
        return courseRepository.findById(id).orElseThrow(() -> new RuntimeException("Course not found"));
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

    public InputStream downloadFile(String fileName) {
        String connectionString = azureStorageProperties.getConnectionString();
        String containerName = azureStorageProperties.getContainerName();

        BlobClient blobClient = new BlobClientBuilder()
                .connectionString(connectionString)
                .containerName(containerName)
                .blobName(fileName)
                .buildClient();

        return blobClient.openInputStream();
    }

    public String getBlobSasUrl(String blobName) {
        String connectionString = azureStorageProperties.getConnectionString();
        String containerName = azureStorageProperties.getContainerName();

        BlobClient blobClient = new BlobClientBuilder()
                .connectionString(connectionString)
                .containerName(containerName)
                .blobName(blobName)
                .buildClient();

        OffsetDateTime expiryTime = OffsetDateTime.now().plusMinutes(1); // Set expiry time for SAS token
        BlobSasPermission permissions = new BlobSasPermission().setReadPermission(true);
        BlobServiceSasSignatureValues sasValues = new BlobServiceSasSignatureValues(expiryTime, permissions)
                .setProtocol(SasProtocol.HTTPS_ONLY);

        String sasToken = blobClient.generateSas(sasValues);
        System.out.println("Generated SAS token: " + sasToken);
        

        return blobClient.getBlobUrl() + "?" + blobClient.generateSas(sasValues);
    }
}
