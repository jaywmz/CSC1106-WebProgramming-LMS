package webprogramming.csc1106.Services;

// Import necessary packages and classes
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.options.BlockBlobOutputStreamOptions;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import com.azure.storage.common.StorageSharedKeyCredential;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webprogramming.csc1106.Config.AzureStorageProperties;

import java.io.IOException;
import java.io.InputStream;
import java.time.OffsetDateTime;

@Service // Indicate that this class is a Spring service component
public class AzureBlobService {

    @Autowired
    private AzureStorageProperties azureStorageProperties; // Inject the AzureStorageProperties

    // Method to get StorageSharedKeyCredential from connection string
    private StorageSharedKeyCredential getStorageSharedKeyCredential() {
        String connectionString = azureStorageProperties.getConnectionString();
        String accountName = connectionString.split(";")[1].split("=")[1]; // Extract account name
        String accountKey = connectionString.split(";")[2].split("=")[1]; // Extract account key

        return new StorageSharedKeyCredential(accountName, accountKey);
    }

    // Method to upload a file to Azure Blob Storage
    public String uploadToAzureBlob(InputStream fileInputStream, String fileName) throws IOException {
        // Build a BlobClient using connection string, container name, and blob name
        BlobClient blobClient = new BlobClientBuilder()
                .connectionString(azureStorageProperties.getConnectionString())
                .containerName(azureStorageProperties.getContainerName())
                .blobName(fileName)
                .buildClient();

        // Set HTTP headers for the blob
        BlockBlobOutputStreamOptions options = new BlockBlobOutputStreamOptions()
                .setHeaders(new BlobHttpHeaders().setContentType("application/octet-stream"));

        // Upload the file to Azure Blob Storage
        try (var blobOutputStream = blobClient.getBlockBlobClient().getBlobOutputStream(options)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                blobOutputStream.write(buffer, 0, bytesRead); // Write data to the blob output stream
            }
        }

        return blobClient.getBlobUrl(); // Return the URL of the uploaded blob
    }

    // Method to generate a SAS URL for a blob
    public String generateSasUrl(String blobUrl) {
        // Validate the blob URL
        if (!blobUrl.startsWith("http://") && !blobUrl.startsWith("https://")) {
            throw new IllegalArgumentException("The Azure Storage Blob endpoint url is malformed.");
        }

        // Build a BlobClient using the blob URL and shared key credential
        BlobClient blobClient = new BlobClientBuilder()
                .endpoint(blobUrl)
                .credential(getStorageSharedKeyCredential())
                .buildClient();

        // Set read permission for the SAS token
        BlobSasPermission permission = new BlobSasPermission().setReadPermission(true);
        // Set the expiration date for the SAS token
        BlobServiceSasSignatureValues values = new BlobServiceSasSignatureValues(OffsetDateTime.now().plusYears(1), permission);

        // Generate the SAS token
        String sasToken = blobClient.generateSas(values);
        return blobClient.getBlobUrl() + "?" + sasToken; // Return the SAS URL
    }

    // Method to delete a blob from Azure Blob Storage
    public void deleteBlob(String blobUrl) {
        // Build a BlobClient using the blob URL and shared key credential
        BlobClient blobClient = new BlobClientBuilder()
                .endpoint(blobUrl)
                .credential(getStorageSharedKeyCredential())
                .buildClient();

        blobClient.delete(); // Delete the blob
    }

    // Method to download a blob from Azure Blob Storage
    public InputStream downloadBlob(String blobUrl) {
        // Build a BlobClient using the blob URL and shared key credential
        BlobClient blobClient = new BlobClientBuilder()
                .endpoint(blobUrl)
                .credential(getStorageSharedKeyCredential())
                .buildClient();

        return blobClient.openInputStream(); // Return the input stream of the blob
    }
}
