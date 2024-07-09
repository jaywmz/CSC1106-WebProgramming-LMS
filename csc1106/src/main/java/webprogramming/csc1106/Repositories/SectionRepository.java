package webprogramming.csc1106.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import webprogramming.csc1106.Entities.Section;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {
    //find by course id
    List<Section> findByCourseId(Long courseId);
    void deleteById(Long id);
    //find by id
    Section findById(long id);
}
