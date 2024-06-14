package webprogramming.csc1106.Entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "lesson")
public class Lesson {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(name = "section_id")
    private Section section;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<FileResource> files = new ArrayList<>();

    @Transient
    private MultipartFile file;

    // Constructors
    public Lesson() {}

    public Lesson(String title, Section section) {
        this.title = title;
        this.section = section;
    }

    // Getters and setters
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
