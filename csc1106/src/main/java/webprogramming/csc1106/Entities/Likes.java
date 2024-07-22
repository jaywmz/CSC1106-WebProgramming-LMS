package webprogramming.csc1106.Entities;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "likes")
public class Likes {
    @EmbeddedId
    private LikesID likesID;

    @MapsId("userID")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @MapsId("postID")
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    public Likes() {}

    public Likes(LikesID likesID, User user, Post post) {
        this.likesID = likesID;
        this.user = user;
        this.post = post;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

}
