package webprogramming.csc1106.Repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import webprogramming.csc1106.Entities.Category;
import java.util.List;

public interface CategoryRepo extends JpaRepository<Category, Integer>{
    List<Category> findByName(String name);
}
