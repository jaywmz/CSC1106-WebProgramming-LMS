package webprogramming.csc1106.Entities;

// Import necessary packages and classes
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity // Indicate that this class is an entity to be mapped to a database table
@Table(name = "upload_course") // Specify the table name in the database
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Ignore specific properties during JSON serialization
public class UploadCourse {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Indicate that the ID should be generated automatically
    @Column(name = "id") // Map the field to the "id" column in the table
    private Long id;

    @Column(name = "title", nullable = false) // Map the field to the "title" column and make it not nullable
    private String title;

    @Column(name = "description", nullable = false)
    @Lob // Indicate that this field should be treated as a large object (LOB)
    private String description;

    @Column(name = "lecturer", nullable = false)
    private String lecturer;

    @Column(name = "price", nullable = false)
    private Double price;

    @Column(name = "cover_image_url", nullable = false)
    private String coverImageUrl;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true) // Establish a one-to-many relationship with Section
    @JsonManagedReference // Handle bidirectional relationship during JSON serialization
    private List<Section> sections = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true) // Establish a one-to-many relationship with Rating
    @JsonManagedReference
    private List<Rating> ratings = new ArrayList<>();

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true) // Establish a one-to-many relationship with CourseCategory
    @JsonManagedReference
    private List<CourseCategory> courseCategories = new ArrayList<>();

    @Transient // Indicate that this field is not to be persisted in the database
    private double averageRating;

    @Transient
    private int reviewCount;

    @Column(name = "is_approved")
    private boolean isApproved;

    @ManyToOne(fetch = FetchType.LAZY) // Establish a many-to-one relationship with User
    @JoinColumn(name = "user_id", nullable = false) // Specify the join column for the relationship
    @JsonIgnore // Ignore this field during JSON serialization
    private User user;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @PrePersist // Annotate a method to be executed before the entity is persisted
    protected void onCreate() {
        createdDate = LocalDateTime.now();
    }

    public UploadCourse() {}

    public UploadCourse(String title, String description, String lecturer, Double price, String coverImageUrl) {
        this.title = title;
        this.description = description;
        this.lecturer = lecturer;
        this.price = price;
        this.coverImageUrl = coverImageUrl;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLecturer() {
        return lecturer;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public List<Section> getSections() {
        return sections;
    }

    public void setSections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(List<Rating> ratings) {
        this.ratings = ratings;
    }

    public List<CourseCategory> getCourseCategories() {
        return courseCategories;
    }

    public void setCourseCategories(List<CourseCategory> courseCategories) {
        this.courseCategories = courseCategories;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean isApproved) {
        this.isApproved = isApproved;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
}
