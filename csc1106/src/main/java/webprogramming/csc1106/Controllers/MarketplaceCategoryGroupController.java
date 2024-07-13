package webprogramming.csc1106.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import webprogramming.csc1106.Entities.CategoryGroup;
import webprogramming.csc1106.Entities.CommunityCategory;
import webprogramming.csc1106.Repositories.CategoryRepo;
import webprogramming.csc1106.Services.CategoryGroupService;

import java.io.IOException;
import java.util.logging.Logger;

@Controller
public class MarketplaceCategoryGroupController {

    @Autowired
    private CategoryGroupService categoryGroupService;
    @Autowired
    private CategoryRepo categoryRepo;

    private static final Logger logger = Logger.getLogger(MarketplaceCategoryGroupController.class.getName());

    @GetMapping("/categories")
    public String showCategoriesPage(Model model) {
        model.addAttribute("categories", categoryGroupService.getAllCategoryGroups());
        model.addAttribute("categoryGroup", new CategoryGroup());
        return "Marketplace/categories";
    }

    @PostMapping("/categories")
    public String addOrUpdateCategory(CategoryGroup categoryGroup, @RequestParam("coverImageFile") MultipartFile coverImageFile, RedirectAttributes redirectAttributes) {
        try {
            categoryGroupService.addCategoryGroup(categoryGroup, coverImageFile);

            CommunityCategory communityCategory = new CommunityCategory(categoryGroup.getName(), categoryGroup.getDescription(), "students");
            categoryRepo.save(communityCategory);

            redirectAttributes.addFlashAttribute("successMessage", "Category group added/updated successfully.");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to upload cover image. Please try again.");
            logger.severe("Error adding/updating category group: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to add/update category group. Please try again.");
            logger.severe("Error adding/updating category group: " + e.getMessage());
        }
        return "redirect:/categories";
    }

    @PostMapping("/delete-category/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryGroupService.deleteCategoryGroup(id);
            redirectAttributes.addFlashAttribute("successMessage", "Category group deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete category group. Please try again.");
            logger.severe("Error deleting category group: " + e.getMessage());
        }
        return "redirect:/categories";
    }

    @GetMapping("/edit-category/{id}")
    public String editCategory(@PathVariable Long id, Model model) {
        CategoryGroup categoryGroup = categoryGroupService.getCategoryGroupById(id);
        model.addAttribute("categoryGroup", categoryGroup);
        model.addAttribute("categories", categoryGroupService.getAllCategoryGroups());
        return "Marketplace/categories";
    }
}
