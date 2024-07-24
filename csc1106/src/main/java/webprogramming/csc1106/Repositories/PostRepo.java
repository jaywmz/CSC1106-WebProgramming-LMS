package webprogramming.csc1106.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

import webprogramming.csc1106.Entities.CommunityCategory;
import webprogramming.csc1106.Entities.Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface PostRepo extends JpaRepository<Post, Long>{
    // Query to find all posts by title or content and order by timestamp. Including pagination
    Page<Post> findAllByTitleContainingOrContentContainingOrderByTimestampDesc(String title, String content, Pageable pageable);

    // Query to find posts by category and order by timestamp. Including pagination
    Page<Post> findAllByCategoryOrderByTimestampDesc(CommunityCategory category, Pageable pageable);

    // Query to find last 5 and order by timestamp
    List<Post> findTop5ByOrderByTimestampDesc();

    // Query to find last 5 and order by likes
    List<Post> findTop5ByOrderByLikesDesc();

    // Query to find all by post id
    Optional<Post> findByPostID(long id);

    // Query to find last 5 by category id and order by timestamp
    List<Post> findTop5ByCategoryIdInOrderByTimestampDesc(List<Integer> categoryIds);

    @Transactional
    void deleteByPostID(long id);
    
    // Query to get category counts
    @Query(value = "SELECT c.id, c.cat_group, c.name, COUNT(p.category_id), MAX(p.timestamp) FROM community_category c \r\n" + //
                "LEFT JOIN post p ON c.id = p.category_id \r\n" + //
                "GROUP BY c.id;", nativeQuery = true)
    List<Object[]> findCategoryCounts();

    // Query to get category counts for students
    @Query(value = "SELECT c.id, c.name, COUNT(p.category_id), MAX(p.timestamp) FROM community_category c \r\n" + 
        "LEFT JOIN post p ON c.id = p.category_id \r\n" + 
        "WHERE c.cat_group = 'students' \r\n" + 
        "GROUP BY c.id;", nativeQuery = true)
    List<Object[]> findCategoryCountsStudents();

    // Query to get category counts for instructors
    @Query(value = "SELECT c.id, c.name, COUNT(p.category_id) FROM community_category c \r\n" + 
        "LEFT JOIN post p ON c.id = p.category_id \r\n" + 
        "WHERE c.cat_group = 'instructors' \r\n" + 
        "GROUP BY c.id;", nativeQuery = true)
    List<Object[]> findCategoryCountsInstructors();

    // Query to find post and order by timestamp
    List<Post> findByOrderByTimestampDesc();

    // Query to find post and order by likes
    List<Post> findByOrderByLikesDesc();
}   
