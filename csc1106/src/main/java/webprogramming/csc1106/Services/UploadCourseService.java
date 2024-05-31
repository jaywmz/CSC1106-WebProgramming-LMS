package webprogramming.csc1106.Services;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.options.BlockBlobOutputStreamOptions;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.azure.storage.common.sas.SasProtocol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import webprogramming.csc1106.Config.AzureStorageProperties;
import webprogramming.csc1106.Entities.UploadCourse;
import webprogramming.csc1106.Repositories.UploadCourseRepository;

import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class UploadCourseService {
    @Autowired
    private UploadCourseRepository courseRepository;

    @Autowired
    private AzureStorageProperties azureStorageProperties;

    public List<UploadCourse> getAllCourses() {
        return courseRepository.findAll();
    }

    public UploadCourse addCourse(UploadCourse course) {
        // Save course metadata to MySQL
        return courseRepository.save(course);
    }

    public UploadCourse addCourseWithFile(UploadCourse course, InputStream fileInputStream, String fileName) throws IOException {
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

    @Async
    public CompletableFuture<Void> deleteCourse(Long courseId) {
        UploadCourse course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Delete the file from Azure Blob Storage
        deleteFromAzureBlob(course.getBlobName());

        // Delete the course metadata from MySQL
        courseRepository.deleteById(courseId);
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

        // Upload the file to Azure Blob Storage using the streaming method
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
        System.out.println("Generating SAS URL for blob: " + blobName);
        String connectionString = azureStorageProperties.getConnectionString();
        String containerName = azureStorageProperties.getContainerName();

        BlobClient blobClient = new BlobClientBuilder()
                .connectionString(connectionString)
                .containerName(containerName)
                .blobName(blobName)
                .buildClient();

        OffsetDateTime expiryTime = OffsetDateTime.now().plusMinutes(1);
        System.out.println("SAS token expiry time: " + expiryTime);

        BlobSasPermission permissions = new BlobSasPermission().setReadPermission(true);
        BlobServiceSasSignatureValues sasValues = new BlobServiceSasSignatureValues(expiryTime, permissions)
                .setProtocol(SasProtocol.HTTPS_ONLY);

        String sasToken = blobClient.generateSas(sasValues);
        System.out.println("Generated SAS token: " + sasToken);

        String sasUrl = blobClient.getBlobUrl() + "?" + sasToken;
        System.out.println("SAS URL: " + sasUrl);

        // Create a new BlobClient with the SAS URL to access the file
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

        OffsetDateTime expiryTime = OffsetDateTime.now().plusHours(1); // Set expiry time for SAS token
        BlobSasPermission permissions = new BlobSasPermission().setReadPermission(true);
        BlobServiceSasSignatureValues sasValues = new BlobServiceSasSignatureValues(expiryTime, permissions)
                .setProtocol(SasProtocol.HTTPS_ONLY);

        return blobClient.getBlobUrl() + "?" + blobClient.generateSas(sasValues);
    }
}
