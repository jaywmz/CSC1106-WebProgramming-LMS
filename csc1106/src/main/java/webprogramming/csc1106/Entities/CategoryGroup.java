package webprogramming.csc1106.Entities;

// Import necessary packages and classes
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;

@Entity // Indicate that this class is an entity to be mapped to a database table
@Table(name = "category_group") // Specify the table name in the database
public class CategoryGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Indicate that the ID should be generated automatically
    @Column(name = "id") // Map the field to the "id" column in the table
    private Long id;

    @Column(name = "name", nullable = false) // Map the field to the "name" column and make it not nullable
    private String name;

    @Column(name = "description") // Map the field to the "description" column
    private String description;

    @Column(name = "cover_image_url") // Map the field to the "cover_image_url" column
    private String coverImageUrl;

    @OneToMany(mappedBy = "categoryGroup", cascade = CascadeType.ALL, orphanRemoval = true) // Establish a one-to-many relationship with CourseCategory
    @JsonManagedReference // Handle bidirectional relationship during JSON serialization
    private List<CourseCategory> courseCategories = new ArrayList<>();

    @Transient // Indicate that this field is not to be persisted in the database
    private int courseCount;

    // Default constructor
    public CategoryGroup() {}

    // Parameterized constructor
    public CategoryGroup(String name, String description, String coverImageUrl) {
        this.name = name;
        this.description = description;
        this.coverImageUrl = coverImageUrl;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public List<CourseCategory> getCourseCategories() {
        return courseCategories;
    }

    public void setCourseCategories(List<CourseCategory> courseCategories) {
        this.courseCategories = courseCategories;
    }

    public int getCourseCount() {
        return courseCount;
    }

    public void setCourseCount(int courseCount) {
        this.courseCount = courseCount;
    }

    // Method to calculate the number of courses in the category
    public void calculateCourseCount() {
        this.courseCount = this.courseCategories.size();
    }
}
