package webprogramming.csc1106.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import webprogramming.csc1106.Entities.Post;

public interface PostRepo extends JpaRepository<Post, Integer>{

}
