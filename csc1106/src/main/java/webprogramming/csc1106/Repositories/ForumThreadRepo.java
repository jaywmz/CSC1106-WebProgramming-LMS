package webprogramming.csc1106.Repositories;

import org.springframework.data.repository.CrudRepository;

import webprogramming.csc1106.Entities.ForumThread;

public interface ForumThreadRepo extends CrudRepository<ForumThread, Integer>{
    
}
