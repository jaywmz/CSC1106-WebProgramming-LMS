package webprogramming.csc1106.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import webprogramming.csc1106.Entities.Comment;

public interface CommentRepo extends JpaRepository<Comment, Integer>{
}