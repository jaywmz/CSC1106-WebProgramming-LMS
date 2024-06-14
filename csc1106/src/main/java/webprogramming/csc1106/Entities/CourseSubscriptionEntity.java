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

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "course_subscriptions")
public class CourseSubscriptionEntity {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private CourseEntity course;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

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

    public CourseSubscriptionEntity(String id, CourseEntity course, User user, String subscriptionStatus) {
        this.id = id;
        this.course = course;
        this.user = user;
        this.subscriptionStatus = subscriptionStatus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CourseEntity getCourse() {
        return course;
    }

    public void setCourse(CourseEntity course) {
        this.course = course;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
