package webprogramming.csc1106.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "course_category")
public class CourseCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    @JsonBackReference
    private UploadCourse course;

    @ManyToOne
    @JoinColumn(name = "category_group_id", nullable = false)
    @JsonBackReference
    private CategoryGroup categoryGroup;

    // Constructors
    public CourseCategory() {}

    public CourseCategory(UploadCourse course, CategoryGroup categoryGroup) {
        this.course = course;
        this.categoryGroup = categoryGroup;
    }

    // Getters and setters
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
