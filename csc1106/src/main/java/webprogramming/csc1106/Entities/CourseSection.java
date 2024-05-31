package webprogramming.csc1106.Entities;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class CourseSection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "section_id")
    private List<Lesson> lessons;

    // Default constructor
    public CourseSection() {
    }

    // Constructor with parameters
    public CourseSection(String title, List<Lesson> lessons) {
        this.title = title;
        this.lessons = lessons;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }
}
