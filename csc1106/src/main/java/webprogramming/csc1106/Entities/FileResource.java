package webprogramming.csc1106.Entities;

// Import necessary packages and classes
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity // Indicate that this class is an entity to be mapped to a database table
@Table(name = "file_resource") // Specify the table name in the database
public class FileResource {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Indicate that the ID should be generated automatically
    @Column(name = "id") // Map the field to the "id" column in the table
    private Long id;

    @Column(name = "file_name", nullable = false) // Map the field to the "file_name" column and make it not nullable
    private String fileName;

    @Column(name = "file_url", nullable = false, length = 2048) // Map the field to the "file_url" column, make it not nullable, and set a maximum length
    private String fileUrl;

    @ManyToOne // Establish a many-to-one relationship with Lesson
    @JoinColumn(name = "lesson_id") // Specify the join column for the relationship
    @JsonBackReference // Handle bidirectional relationship during JSON serialization
    private Lesson lesson;

    @Transient // Indicate that this field is not to be persisted in the database
    private String fileType;

    @Transient
    private String content;

    // Default constructor
    public FileResource() {}

    // Parameterized constructor
    public FileResource(String fileName, String fileUrl) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
