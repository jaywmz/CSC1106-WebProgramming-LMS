package webprogramming.csc1106.Entities;

import java.sql.Timestamp;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long postID;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private CommunityCategory category;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostAttachments> attachments;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Likes> likedBy;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Subscription> subscribedBy;

    @Column(name = "poster_name", nullable = false)
    private String posterName; // username that posted the thread

    @Column(name = "timestamp", nullable = false)
    private Timestamp timestamp;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "likes", columnDefinition = "integer default 0")
    private Integer likes = 0;

    public Post() {};

    public Post(String posterName, Timestamp timestamp, String title, String content, CommunityCategory category) {
        this.posterName = posterName;
        this.timestamp = timestamp;
        this.title = title;
        this.content = content;
        this.category = category;
    }

    // Getters and setters
    public Long getPostID() {
        return postID;
    }

    public void setPostID(Long id) {
        this.postID = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CommunityCategory getCategory() {
        return category;
    }

    public void setCategory(CommunityCategory category) {
        this.category = category;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public List<PostAttachments> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<PostAttachments> attachments) {
        this.attachments = attachments;
    }

    public List<Likes> getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(List<Likes> likedBy) {
        this.likedBy = likedBy;
    }

    public String getPosterName() {
        return posterName;
    }

    public void setPosterName(String posterName) {
        this.posterName = posterName;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
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

    public void setContent(String content) {
        this.content = content;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
}
