package webprogramming.csc1106.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import webprogramming.csc1106.Entities.Rating;

public interface RatingRepository extends JpaRepository<Rating, Long> {
}
