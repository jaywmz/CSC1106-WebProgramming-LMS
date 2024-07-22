package webprogramming.csc1106.Repositories;

// Import necessary packages and classes
import org.springframework.data.jpa.repository.JpaRepository;
import webprogramming.csc1106.Entities.FileResource;

public interface FileResourceRepository extends JpaRepository<FileResource, Long> {
    // This interface inherits all the methods for CRUD operations from JpaRepository
}
