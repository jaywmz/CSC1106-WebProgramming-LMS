package webprogramming.csc1106.Repositories;

// Import necessary packages and classes
import org.springframework.data.jpa.repository.JpaRepository;
import webprogramming.csc1106.Entities.CategoryGroup;

public interface CategoryGroupRepository extends JpaRepository<CategoryGroup, Long> {

    // Find a CategoryGroup entity by its ID
    CategoryGroup findById(int id);
}
