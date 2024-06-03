package webprogramming.csc1106.Entities;

import jakarta.persistence.*;

@Entity
public class CourseCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private UploadCourse course;

    @ManyToOne
    @JoinColumn(name = "category_group_id")
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
