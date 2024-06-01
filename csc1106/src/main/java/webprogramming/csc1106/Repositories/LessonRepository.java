package webprogramming.csc1106.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import webprogramming.csc1106.Entities.Lesson;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
}
