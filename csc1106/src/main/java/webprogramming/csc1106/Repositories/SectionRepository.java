package webprogramming.csc1106.Repositories;

// Import necessary packages and classes
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import webprogramming.csc1106.Entities.Section;

@Repository // Indicate that this interface is a Spring Data JPA repository
public interface SectionRepository extends JpaRepository<Section, Long> {

    // Find all Section entities by a specific course ID
    List<Section> findByCourseId(Long courseId);

    // Delete a Section entity by its ID
    void deleteById(Long id);

    // Find a Section entity by its ID
    Section findById(long id);
}
