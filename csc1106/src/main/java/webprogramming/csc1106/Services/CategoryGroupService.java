package webprogramming.csc1106.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webprogramming.csc1106.Entities.CategoryGroup;
import webprogramming.csc1106.Repositories.CategoryGroupRepository;

@Service
public class CategoryGroupService {

    @Autowired
    private CategoryGroupRepository categoryGroupRepository;

    public void addCategoryGroup(CategoryGroup categoryGroup) {
        categoryGroupRepository.save(categoryGroup);
    }
}
