package webprogramming.csc1106.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import webprogramming.csc1106.Entities.Comment;
import webprogramming.csc1106.Entities.Post;


public interface CommentRepo extends JpaRepository<Comment, Integer>{
    @Transactional
    void deleteByPost(Post post);
}
