package webprogramming.csc1106.Repositories;

// Import necessary packages and classes
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import webprogramming.csc1106.Entities.Lesson;

@Repository // Indicate that this interface is a Spring Data JPA repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    // Find all Lesson entities by a specific section ID
    List<Lesson> findBySectionId(Long sectionId);

    // Save a Lesson entity
    Lesson save(Lesson lesson);

    // Delete a Lesson entity by its ID
    void deleteById(Long id);

    // Find a Lesson entity by its ID
    Lesson findById(long id);
}
