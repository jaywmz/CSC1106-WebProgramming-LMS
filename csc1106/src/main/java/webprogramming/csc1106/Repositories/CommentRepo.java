package webprogramming.csc1106.Repositories;

import org.springframework.data.repository.CrudRepository;

import webprogramming.csc1106.Entities.Comment;

public interface CommentRepo extends CrudRepository<Comment, Integer>{
}
