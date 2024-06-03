package webprogramming.csc1106.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "post_attachments")
public class PostAttachments {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "uri", nullable = false)
    private String URI;

    // Constructors
    public PostAttachments() {}

    public PostAttachments(Post post, String URI) {
        this.post = post;
        this.URI = URI;
    }

    // Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public String getURI() {
        return URI;
    }

    public void setURI(String URI) {
        this.URI = URI;
    }
}
