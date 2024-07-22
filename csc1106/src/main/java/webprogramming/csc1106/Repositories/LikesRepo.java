package webprogramming.csc1106.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import webprogramming.csc1106.Entities.Likes;
import webprogramming.csc1106.Entities.LikesID;

public interface LikesRepo extends JpaRepository<Likes, LikesID>{
    
}