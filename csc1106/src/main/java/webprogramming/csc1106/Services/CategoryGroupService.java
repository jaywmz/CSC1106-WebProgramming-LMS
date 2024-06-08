package webprogramming.csc1106.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webprogramming.csc1106.Entities.CategoryGroup;
import webprogramming.csc1106.Repositories.CategoryGroupRepository;

import java.util.List;

@Service
public class CategoryGroupService {

    @Autowired
    private CategoryGroupRepository categoryGroupRepository;

    public void addCategoryGroup(CategoryGroup categoryGroup) {
        categoryGroupRepository.save(categoryGroup);
    }

    public List<CategoryGroup> getAllCategoryGroups() {
        return categoryGroupRepository.findAll();
    }

    public void deleteCategoryGroup(Long id) {
        categoryGroupRepository.deleteById(id);
    }

    public CategoryGroup getCategoryGroupById(Long id) {
        return categoryGroupRepository.findById(id).orElse(null);
    }
}
