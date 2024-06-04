package webprogramming.csc1106.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import java.util.List;
import org.springframework.data.domain.Page;
import webprogramming.csc1106.Entities.Post;
import org.springframework.data.jpa.repository.Query;

public interface PostRepo extends JpaRepository<Post, Integer>{
    Page<Post> findAllByTitleContainingOrContentContainingOrderByTimestampDesc(String title, String content, Pageable pageable);
    Page<Post> findAllByOrderByTimestampDesc(Pageable pageable);
    Post findById(long id);
    
    @Query(value = "SELECT category_id, COUNT(category_id) FROM post GROUP BY category_id", nativeQuery = true)
    List<Object[]> findCategoryCounts();
}   
