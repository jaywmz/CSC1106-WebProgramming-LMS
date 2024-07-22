package webprogramming.csc1106.Entities;

// Import necessary packages and classes
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;

@Entity // Indicate that this class is an entity to be mapped to a database table
@Table(name = "section") // Specify the table name in the database
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Indicate that the ID should be generated automatically
    @Column(name = "id") // Map the field to the "id" column in the table
    private Long id;

    @Column(name = "title", nullable = false) // Map the field to the "title" column and make it not nullable
    private String title;

    @Column(name = "description", nullable = false) // Map the field to the "description" column and make it not nullable
    private String description;

    @ManyToOne // Establish a many-to-one relationship with UploadCourse
    @JoinColumn(name = "course_id") // Specify the join column for the relationship
    @JsonBackReference // Handle bidirectional relationship during JSON serialization
    private UploadCourse course;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true) // Establish a one-to-many relationship with Lesson
    @JsonManagedReference // Handle bidirectional relationship during JSON serialization
    private List<Lesson> lessons = new ArrayList<>();

    // Default constructor
    public Section() {}

    // Parameterized constructor
    public Section(String title, String description, UploadCourse course) {
        this.title = title;
        this.description = description;
        this.course = course;
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

    public UploadCourse getCourse() {
        return course;
    }

    public void setCourse(UploadCourse course) {
        this.course = course;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }
}
