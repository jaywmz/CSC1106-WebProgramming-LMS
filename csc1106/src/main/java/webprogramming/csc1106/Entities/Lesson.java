package webprogramming.csc1106.Entities;

// Import necessary packages and classes
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.ArrayList;

@Entity // Indicate that this class is an entity to be mapped to a database table
@Table(name = "lesson") // Specify the table name in the database
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Indicate that the ID should be generated automatically
    @Column(name = "id") // Map the field to the "id" column in the table
    private Long id;

    @Column(name = "title", nullable = false) // Map the field to the "title" column and make it not nullable
    private String title;

    @ManyToOne // Establish a many-to-one relationship with Section
    @JoinColumn(name = "section_id") // Specify the join column for the relationship
    @JsonBackReference // Handle bidirectional relationship during JSON serialization
    private Section section;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true) // Establish a one-to-many relationship with FileResource
    @JsonManagedReference // Handle bidirectional relationship during JSON serialization
    private List<FileResource> files = new ArrayList<>();

    @Transient // Indicate that this field is not to be persisted in the database
    private MultipartFile file;

    // Default constructor
    public Lesson() {}

    // Parameterized constructor
    public Lesson(String title, Section section) {
        this.title = title;
        this.section = section;
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

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public List<FileResource> getFiles() {
        return files;
    }

    public void setFiles(List<FileResource> files) {
        this.files = files;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
