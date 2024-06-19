package webprogramming.csc1106.Entities;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "cart_items")
public class CartItemEntity {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "course_id", nullable = false)
    private long courseId;

    @Column(name = "course_name", nullable = false)
    private String courseName;

    @Column(name = "course_instructor", nullable = false)
    private String courseInstructor;

    @Column(name = "course_category", nullable = false)
    private String courseCategory;

    @Column(name = "course_price", nullable = false)
    private BigDecimal coursePrice;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "added_at", nullable = false)
    @CreationTimestamp
    private Timestamp addedAt;

    public CartItemEntity() {
    }

    public CartItemEntity(String id, long courseId, int userId) {
        this.id = id;
        this.courseId = courseId;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseInstructor() {
        return courseInstructor;
    }

    public void setCourseInstructor(String courseInstructor) {
        this.courseInstructor = courseInstructor;
    }

    public String getCourseCategory() {
        return courseCategory;
    }

    public void setCourseCategory(String courseCategory) {
        this.courseCategory = courseCategory;
    }

    public BigDecimal getCoursePrice() {
        return coursePrice;
    }

    public void setCoursePrice(BigDecimal coursePrice) {
        this.coursePrice = coursePrice;
    }
    
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Timestamp getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(Timestamp addedAt) {
        this.addedAt = addedAt;
    }

    //Generate a random ID with a specific length
    public String generateRandomId(int length) {
        String alphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                                    + "0123456789";

        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = new Random().nextInt(alphaNumericString.length());
            sb.append(alphaNumericString.charAt(index));
        }

        return sb.toString();
    }
}
