package webprogramming.csc1106.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import webprogramming.csc1106.Entities.Lesson;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
    //find by section id
    List<Lesson> findBySectionId(Long sectionId);
    //save lesson respo .save lesson
    Lesson save(Lesson lesson);

    void deleteById(Long id);

    //find by id
    Lesson findById(long id);

}
