package webprogramming.csc1106.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import webprogramming.csc1106.Entities.FileResource;

public interface FileResourceRepository extends JpaRepository<FileResource, Long> {
}
