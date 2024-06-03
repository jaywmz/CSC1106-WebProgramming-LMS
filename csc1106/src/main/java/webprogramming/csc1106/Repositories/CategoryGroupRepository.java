package webprogramming.csc1106.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import webprogramming.csc1106.Entities.CategoryGroup;

public interface CategoryGroupRepository extends JpaRepository<CategoryGroup, Long> {
}
