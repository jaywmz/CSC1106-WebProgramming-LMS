package webprogramming.csc1106.Entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double score;
    private String comment;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private UploadCourse course;

    private LocalDateTime timestamp;

    // Constructors
    public Rating() {}

    public Rating(Double score, String comment, UploadCourse course) {
        this.score = score;
        this.comment = comment;
        this.course = course;
        this.timestamp = LocalDateTime.now(); // Set the current timestamp
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public UploadCourse getCourse() {
        return course;
    }

    public void setCourse(UploadCourse course) {
        this.course = course;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
