package webprogramming.csc1106.Entities;

import java.sql.Timestamp;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "comment")
public class Comment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id; 

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    // @OneToMany(mappedBy = "parent")
    // private List<Comment> replies;

    // @ManyToOne
    // @JoinColumn(name = "parent_id")
    // private Comment parent;

    @Column(name = "commenter_name", nullable = false)
    private String commenterName; // name of user that posted the thread

    @Column(name = "timestamp", nullable = false)
    private Timestamp timestamp;

    @Column(name = "content", nullable = false)
    private String content;

    // Getters and setters
    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public String getCommenterName() {
        return commenterName;
    }

    public void setCommenterName(String commenterName) {
        this.commenterName = commenterName;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }

    // public List<Comment> getReplies() {
    //     return replies;
    // }

    // public void setReplies(List<Comment> replies) {
    //     this.replies = replies;
    // }

    // public Comment getParent() {
    //     return parent;
    // }

    // public void setParent(Comment parent) {
    //     this.parent = parent;
    // }

    public long getId() {
        return id;
    }
}
