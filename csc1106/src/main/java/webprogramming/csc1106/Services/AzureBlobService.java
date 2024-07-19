package webprogramming.csc1106.Services;

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

@Service
public class AzureBlobService {

    @Autowired
    private AzureStorageProperties azureStorageProperties;

    private StorageSharedKeyCredential getStorageSharedKeyCredential() {
        String connectionString = azureStorageProperties.getConnectionString();
        String accountName = connectionString.split(";")[1].split("=")[1];
        String accountKey = connectionString.split(";")[2].split("=")[1];

        return new StorageSharedKeyCredential(accountName, accountKey);
    }

    public String uploadToAzureBlob(InputStream fileInputStream, String fileName) throws IOException {
        BlobClient blobClient = new BlobClientBuilder()
                .connectionString(azureStorageProperties.getConnectionString())
                .containerName(azureStorageProperties.getContainerName())
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

    public String generateSasUrl(String blobUrl) {
        if (!blobUrl.startsWith("http://") && !blobUrl.startsWith("https://")) {
            throw new IllegalArgumentException("The Azure Storage Blob endpoint url is malformed.");
        }

        BlobClient blobClient = new BlobClientBuilder()
                .endpoint(blobUrl)
                .credential(getStorageSharedKeyCredential())
                .buildClient();

        BlobSasPermission permission = new BlobSasPermission().setReadPermission(true);
        BlobServiceSasSignatureValues values = new BlobServiceSasSignatureValues(OffsetDateTime.now().plusYears(1), permission);

        String sasToken = blobClient.generateSas(values);
        return blobClient.getBlobUrl() + "?" + sasToken;
    }

    public void deleteBlob(String blobUrl) {
        BlobClient blobClient = new BlobClientBuilder()
                .endpoint(blobUrl)
                .credential(getStorageSharedKeyCredential())
                .buildClient();

        blobClient.delete();
    }

    public InputStream downloadBlob(String blobUrl) {
        BlobClient blobClient = new BlobClientBuilder()
                .endpoint(blobUrl)
                .credential(getStorageSharedKeyCredential())
                .buildClient();

        return blobClient.openInputStream();
    }
}
