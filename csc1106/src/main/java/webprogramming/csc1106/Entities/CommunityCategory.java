package webprogramming.csc1106.Entities;

import jakarta.persistence.*;
import java.util.List;
import java.util.Comparator;


@Entity
@Table(name = "community_category")
public class CommunityCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = true)
    private String description;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Post> posts;

    // Constructors
    public CommunityCategory() {}

    public CommunityCategory(String name) {
        this.name = name;
    }

    // Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Post> getPosts() {
        posts.sort(Comparator.comparing(Post::getTimestamp).reversed());
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }
}
