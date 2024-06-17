package webprogramming.csc1106.Models;

import java.io.Serializable;

import jakarta.persistence.Embeddable;

@Embeddable
public class LikesID implements Serializable{
    private Integer userID;
    private Long postID;

    public LikesID() {}

    public LikesID(int userID, long postID) {
        this.userID = userID;
        this.postID = postID;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }
    
    public Long getPostID() {
        return postID;
    }
    
    public void setPostID(Long postID) {
        this.postID = postID;
    }

    @Override
    public int hashCode() {
        return 13;
    }
 
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LikesID other = (LikesID) obj;
        return (userID != null && postID != null) && (userID.equals(other.getUserID()) && postID.equals(other.getPostID()));
    }

}