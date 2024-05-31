package webprogramming.csc1106.Entities;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "lesson_id")
    private List<FileResource> files;

    // Default constructor
    public Lesson() {
    }

    // Constructor with parameters
    public Lesson(String title, String content, List<FileResource> files) {
        this.title = title;
        this.content = content;
        this.files = files;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<FileResource> getFiles() {
        return files;
    }

    public void setFiles(List<FileResource> files) {
        this.files = files;
    }
}
