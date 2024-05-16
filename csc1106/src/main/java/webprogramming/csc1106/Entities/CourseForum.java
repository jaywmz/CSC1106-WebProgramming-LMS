package webprogramming.csc1106.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class CourseForum {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int forumID; // primary key

    // need to map to courses(courseID), tbc)
    private int courseID; // foreign key to Courses table
    private String description; // for forum description to set rules and stuff

    public CourseForum() {
    }

    public CourseForum(int forumID, int courseID, String description) {
        this.forumID = forumID;
        this.courseID = courseID;
        this.description = description;
    }
    
    public int getCourseID() {
        return courseID;
    }

    // public void setCourseID(int courseID) {
    //     this.courseID = courseID;
    // }

    public int getForumID() {
        return forumID;
    }

    // public void setForumID(int numOfThreads) {
    //     this.forumID = numOfThreads;
    // }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
}
