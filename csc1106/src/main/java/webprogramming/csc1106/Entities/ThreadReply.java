package webprogramming.csc1106.Entities;

import java.sql.Date;
import java.sql.Time;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class ThreadReply {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int replyID; // primary key

    // need to map to ThreadReply(replyID), self-referential
    private int commentID; // replyID of comment that is being replied to

    // need to map to ForumThread(threadID)
    private int threadID; // foreign key to thread table

    private String responderName; // name of user that posted the thread
    private Date replyDate;
    private Time replyTime;
    private String replyContent;

    public ThreadReply() {
    }

    public ThreadReply(int replyID, int commentID, int threadID, String responderName, Date replyDate, Time replyTime,
            String replyContent) {
        this.replyID = replyID;
        this.commentID = commentID;
        this.threadID = threadID;
        this.responderName = responderName;
        this.replyDate = replyDate;
        this.replyTime = replyTime;
        this.replyContent = replyContent;
    }

    public int getReplyID() {
        return replyID;
    }

    // public void setReplyID(int replyID) {
    //     this.replyID = replyID;
    // }

    public int getCommentID() {
        return commentID;
    }

    // public void setCommentID(int commentID) {
    //     this.commentID = commentID;
    // }

    public int getThreadID() {
        return threadID;
    }

    // public void setThreadID(int threadID) {
    //     this.threadID = threadID;
    // }

    public String getResponderName() {
        return responderName;
    }

    public void setResponderName(String responderName) {
        this.responderName = responderName;
    }

    public Date getReplyDate() {
        return replyDate;
    }

    public void setReplyDate(Date replyDate) {
        this.replyDate = replyDate;
    }

    public Time getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(Time replyTime) {
        this.replyTime = replyTime;
    }

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }

}
