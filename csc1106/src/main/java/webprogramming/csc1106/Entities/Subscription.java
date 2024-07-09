package webprogramming.csc1106.Entities;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import webprogramming.csc1106.Models.SubscribeID;

@Entity
@Table(name = "subscriptions")
public class Subscription {
    @EmbeddedId
    private SubscribeID subID;

    @MapsId("userID")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @MapsId("postID")
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    public Subscription() {}

    public Subscription(SubscribeID subID, User user, Post post) {
        this.subID = subID;
        this.user = user;
        this.post = post;
    }

    public SubscribeID getSubID() {
        return subID;
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
