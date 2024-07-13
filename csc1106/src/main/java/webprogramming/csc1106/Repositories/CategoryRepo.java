package webprogramming.csc1106.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import webprogramming.csc1106.Entities.CommunityCategory;

public interface CategoryRepo extends JpaRepository<CommunityCategory, Integer>{
    CommunityCategory findByNameIgnoreCase(String name);
    CommunityCategory findById(int id);
}
