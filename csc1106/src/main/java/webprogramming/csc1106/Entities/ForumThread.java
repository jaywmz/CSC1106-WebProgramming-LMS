package webprogramming.csc1106.Entities;

import java.sql.Date;
import java.sql.Time;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class ForumThread {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int threadID; // primary key

    // need to map to CourseForum(forumID)
    private int forumID; // foreign key to CourseForum table

    private String posterName; // name of user that posted the thread
    private Date postDate; 
    private Time postTime;
    private String title;
    private String content;

    public ForumThread() {
    }

    public ForumThread(int threadID, int forumID, String posterName, Date postDate, Time postTime, int replies,
            String title, String subject) {
        this.threadID = threadID;
        this.forumID = forumID;
        this.posterName = posterName;
        this.postDate = postDate;
        this.postTime = postTime;
        this.title = title;
        this.content = subject;
    }

    public int getThreadID() {
        return threadID;
    }

    // public void setThreadID(int threadID) {
    //     this.threadID = threadID;
    // }

    public int getForumID() {
        return forumID;
    }

    // public void setForumID(int courseID) {
    //     this.forumID = courseID;
    // }

    public String getPosterName() {
        return posterName;
    }

    public void setPosterName(String posterName) {
        this.posterName = posterName;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    public Time getPostTime() {
        return postTime;
    }

    public void setPostTime(Time postTime) {
        this.postTime = postTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String subject) {
        this.content = subject;
    }
    
}
