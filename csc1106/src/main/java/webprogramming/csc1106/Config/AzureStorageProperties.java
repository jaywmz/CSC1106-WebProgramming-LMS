package webprogramming.csc1106.Config;

// Import necessary packages and classes
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component // Indicate that this class is a Spring component
@ConfigurationProperties(prefix = "azure.storage") // Bind properties with the prefix "azure.storage" to this class
public class AzureStorageProperties {
    private String connectionString;
    private String containerName;

    // Getters and Setters

    // Get the connection string for Azure storage
    public String getConnectionString() {
        return connectionString;
    }

    // Set the connection string for Azure storage
    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    // Get the container name for Azure storage
    public String getContainerName() {
        return containerName;
    }

    // Set the container name for Azure storage
    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }
}
