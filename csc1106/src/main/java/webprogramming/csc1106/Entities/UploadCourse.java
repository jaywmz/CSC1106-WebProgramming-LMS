package webprogramming.csc1106.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class UploadCourse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String lecturer;
    private Double price;
    private String status;
    private String blobUrl; // URL to the blob storage
    private String blobName; // Name of the blob

    // Default constructor
    public UploadCourse() {
    }

    // Constructor with parameters
    public UploadCourse(String title, String description, String lecturer, Double price, String status, String blobUrl, String blobName) {
        this.title = title;
        this.description = description;
        this.lecturer = lecturer;
        this.price = price;
        this.status = status;
        this.blobUrl = blobUrl;
        this.blobName = blobName;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLecturer() {
        return lecturer;
    }

    public Double getPrice() {
        return price;
    }

    public String getStatus() {
        return status;
    }
    public String getBlobUrl() {
        return blobUrl;
    }

    public String getBlobName() {
        return blobName;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public void setBlobUrl(String blobUrl) {
        this.blobUrl = blobUrl;
    }

    public void setBlobName(String blobName) {
        this.blobName = blobName;
    }
}
