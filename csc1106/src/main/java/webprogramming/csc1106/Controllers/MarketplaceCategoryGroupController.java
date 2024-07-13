package webprogramming.csc1106.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import webprogramming.csc1106.Entities.CategoryGroup;
import webprogramming.csc1106.Services.CategoryGroupService;

import java.io.IOException;
import java.util.logging.Logger;

@Controller
public class MarketplaceCategoryGroupController {

    @Autowired
    private CategoryGroupService categoryGroupService;

    private static final Logger logger = Logger.getLogger(MarketplaceCategoryGroupController.class.getName());

    @GetMapping("/add-category")
    public String showAddCategoryPage(Model model) {
        model.addAttribute("categoryGroup", new CategoryGroup());
        return "Marketplace/add-category";
    }

    @PostMapping("/add-category")
    public String addCategory(CategoryGroup categoryGroup, @RequestParam("coverImageFile") MultipartFile coverImageFile, Model model, RedirectAttributes redirectAttributes) {
        try {
            categoryGroupService.addCategoryGroup(categoryGroup, coverImageFile);
            redirectAttributes.addFlashAttribute("successMessage", "Category group added successfully.");
            return "redirect:/market";
        } catch (IOException e) {
            model.addAttribute("errorMessage", "Failed to upload cover image. Please try again.");
            logger.severe("Error adding category group: " + e.getMessage());
            return "Marketplace/add-category";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Failed to add category group. Please try again.");
            logger.severe("Error adding category group: " + e.getMessage());
            return "Marketplace/add-category";
        }
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
        return "redirect:/market";
    }

    @GetMapping("/categories")
    public String showCategoriesPage(Model model) {
        model.addAttribute("categories", categoryGroupService.getAllCategoryGroups());
        return "Marketplace/categories";
    }
}
