package webprogramming.csc1106.Models;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import webprogramming.csc1106.Entities.CourseCategoriesEntity;

// JBDC
public class Course {
    private int id;
    private String name;
    private String description;
    private BigDecimal price;
    private int duration;
    private String image_url;
    private String instructor;
    private Timestamp created_at;
    private Timestamp updated_at;
    private int category_id;

    // Add other fields as needed

    public Course() {
    }

    public Course(String name, String description, BigDecimal price, int duration, String image_url, String instructor, Timestamp created_at, Timestamp updated_at, int category_id) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.duration = duration;
        this.image_url = image_url;
        this.instructor = instructor;
        this.created_at = created_at;
        this.updated_at = updated_at;
        // this.category_id = category
    }

    // Getters and setters for all fields
    public int getId() {
        return id;
    }

    // Auto increment (no need to set)
    // public void setId(int id) {
    //     this.id = id;
    // }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    public Timestamp getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(Timestamp updated_at) {
        this.updated_at = updated_at;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    // Add other getters and setters as needed
}