package webprogramming.csc1106.Repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import webprogramming.csc1106.Entities.Post;

public interface CategoryRepo extends JpaRepository<Post, Integer>{

}
