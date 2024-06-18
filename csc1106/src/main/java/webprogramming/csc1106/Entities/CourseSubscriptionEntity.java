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
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "course_subscriptions")
public class CourseSubscriptionEntity {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "course_id", nullable = false)
    private long courseId;

    @Column(name = "course_name", nullable = false)
    private String courseName;

    @Column(name = "course_instructor", nullable = false)
    private String courseInstructor;

    @Column(name = "course_cover_image_url", nullable = true)
    private String courseCoverImageUrl;

    @Column(name= "course_description", nullable = true)
    @Lob
    private String courseDescription;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "subscription_date", nullable = false)
    @CreationTimestamp
    private Timestamp subscriptionDate;

    @Column(name = "subscription_status", nullable = false)
    private String subscriptionStatus;

    @Column(name = "completed_date", nullable = true)
    private Timestamp completedDate;

    @Column(name = "recently_updated", nullable = true)
    @UpdateTimestamp
    private Timestamp recentlyUpdated;

    public CourseSubscriptionEntity() {
    }

    public CourseSubscriptionEntity(String id, long courseId, int userId, String subscriptionStatus) {
        this.id = id;
        this.courseId = courseId;
        this.userId = userId;
        this.subscriptionStatus = subscriptionStatus;
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

    public String getCourseCoverImageUrl() {
        return courseCoverImageUrl;
    }

    public void setCourseCoverImageUrl(String courseCoverImageUrl) {
        this.courseCoverImageUrl = courseCoverImageUrl;
    }

    public String getCourseDescription() {
        return courseDescription;
    }

    public void setCourseDescription(String courseDescription) {
        this.courseDescription = courseDescription;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Timestamp getSubscriptionDate() {
        return subscriptionDate;
    }

    public void setSubscriptionDate(Timestamp subscriptionDate) {
        this.subscriptionDate = subscriptionDate;
    }

    public String getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public void setSubscriptionStatus(String subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
    }

    public Timestamp getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(Timestamp completedDate) {
        this.completedDate = completedDate;
    }

    public Timestamp getRecentlyUpdated() {
        return recentlyUpdated;
    }

    public void setRecentlyUpdated(Timestamp recentlyUpdated) {
        this.recentlyUpdated = recentlyUpdated;
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