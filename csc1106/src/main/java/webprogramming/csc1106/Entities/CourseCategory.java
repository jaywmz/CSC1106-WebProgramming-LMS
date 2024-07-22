package webprogramming.csc1106.Entities;

// Import necessary packages and classes
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity // Indicate that this class is an entity to be mapped to a database table
@Table(name = "course_category") // Specify the table name in the database
public class CourseCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Indicate that the ID should be generated automatically
    @Column(name = "id") // Map the field to the "id" column in the table
    private Long id;

    @ManyToOne // Establish a many-to-one relationship with UploadCourse
    @JoinColumn(name = "course_id", nullable = false) // Specify the join column for the relationship and make it not nullable
    @JsonBackReference // Handle bidirectional relationship during JSON serialization
    private UploadCourse course;

    @ManyToOne // Establish a many-to-one relationship with CategoryGroup
    @JoinColumn(name = "category_group_id", nullable = false) // Specify the join column for the relationship and make it not nullable
    @JsonBackReference // Handle bidirectional relationship during JSON serialization
    private CategoryGroup categoryGroup;

    // Default constructor
    public CourseCategory() {}

    // Parameterized constructor
    public CourseCategory(UploadCourse course, CategoryGroup categoryGroup) {
        this.course = course;
        this.categoryGroup = categoryGroup;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UploadCourse getCourse() {
        return course;
    }

    public void setCourse(UploadCourse course) {
        this.course = course;
    }

    public CategoryGroup getCategoryGroup() {
        return categoryGroup;
    }

    public void setCategoryGroup(CategoryGroup categoryGroup) {
        this.categoryGroup = categoryGroup;
    }
}
