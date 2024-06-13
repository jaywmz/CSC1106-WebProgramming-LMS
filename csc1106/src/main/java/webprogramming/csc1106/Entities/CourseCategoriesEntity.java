package webprogramming.csc1106.Entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;

@Entity
@Table(name = "course_categories")
public class CourseCategoriesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "category_name")
    private String category_name;

    @Column(name = "category_img", columnDefinition = "LONGTEXT", nullable = true)
    private String category_img;

    public CourseCategoriesEntity() {
    }

    public CourseCategoriesEntity(String category_name, String category_img) {
        this.category_name = category_name;
        this.category_img = category_img;
    }

    public int getId() {
        return id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getCategory_img() {
        return category_img;
    }

    public void setCategory_img(String category_img) {
        this.category_img = category_img;
    }
}
