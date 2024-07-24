package webprogramming.csc1106.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import webprogramming.csc1106.Entities.CommunityCategory;

public interface CategoryRepo extends JpaRepository<CommunityCategory, Integer>{
    // Query to find category by name
    CommunityCategory findByNameIgnoreCase(String name);
    CommunityCategory findByName(String name);

    // Query to find category by id
    Optional<CommunityCategory> findById(int id);

    // Query to find category by group
    List<CommunityCategory> findByGroup(String group);
}
