package webprogramming.csc1106.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import java.util.List;
import org.springframework.data.domain.Page;
import webprogramming.csc1106.Entities.Post;
import org.springframework.data.jpa.repository.Query;

public interface PostRepo extends JpaRepository<Post, Integer>{
    Page<Post> findAllByTitleContainingOrContentContainingOrderByTimestampDesc(String title, String content, Pageable pageable);
    List<Post> findTop5ByOrderByTimestampDesc();
    Post findById(long id);
    
    @Query(value = "SELECT c.id, c.description, COUNT(p.category_id) FROM community_category c \r\n" + //
                "LEFT JOIN post p ON c.id = p.category_id \r\n" + //
                "GROUP BY c.id;", nativeQuery = true)
    List<Object[]> findCategoryCounts();
}   
