package webprogramming.csc1106.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import java.util.List;
import org.springframework.data.domain.Page;
import webprogramming.csc1106.Entities.Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface PostRepo extends JpaRepository<Post, Long>{
    Page<Post> findAllByTitleContainingOrContentContainingOrderByTimestampDesc(String title, String content, Pageable pageable);
    List<Post> findTop5ByOrderByTimestampDesc();
    Post findByPostID(long id);
    List<Post> findTop5ByCategoryIdInOrderByTimestampDesc(List<Integer> categoryIds);
    @Transactional
    void deleteByPostID(long id);
    
    @Query(value = "SELECT c.id, c.cat_group, c.name, COUNT(p.category_id), MAX(p.timestamp) FROM community_category c \r\n" + //
                "LEFT JOIN post p ON c.id = p.category_id \r\n" + //
                "GROUP BY c.id;", nativeQuery = true)
    List<Object[]> findCategoryCounts();

    @Query(value = "SELECT c.id, c.name, COUNT(p.category_id), MAX(p.timestamp) FROM community_category c \r\n" + 
        "LEFT JOIN post p ON c.id = p.category_id \r\n" + 
        "WHERE c.cat_group = 'students' \r\n" + 
        "GROUP BY c.id;", nativeQuery = true)
    List<Object[]> findCategoryCountsStudents();

    @Query(value = "SELECT c.id, c.name, COUNT(p.category_id) FROM community_category c \r\n" + 
        "LEFT JOIN post p ON c.id = p.category_id \r\n" + 
        "WHERE c.cat_group = 'instructors' \r\n" + 
        "GROUP BY c.id;", nativeQuery = true)
    List<Object[]> findCategoryCountsInstructors();
}   
