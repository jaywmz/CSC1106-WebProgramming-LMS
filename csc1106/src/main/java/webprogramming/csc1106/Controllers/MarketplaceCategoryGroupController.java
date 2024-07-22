package webprogramming.csc1106.Controllers;

// Import necessary packages and classes
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

@Controller // Indicate that this class is a Spring MVC controller
public class MarketplaceCategoryGroupController {

    @Autowired
    private CategoryGroupService categoryGroupService; // Inject the CategoryGroupService
    @Autowired
    private CategoryRepo categoryRepo; // Inject the CategoryRepo

    private static final Logger logger = Logger.getLogger(MarketplaceCategoryGroupController.class.getName()); // Initialize a logger

    // Handle GET requests to "/categories" URL
    @GetMapping("/categories")
    public String showCategoriesPage(Model model) {
        model.addAttribute("categories", categoryGroupService.getAllCategoryGroups()); // Add all category groups to the model
        model.addAttribute("categoryGroup", new CategoryGroup()); // Add a new CategoryGroup object to the model
        return "Marketplace/categories"; // Return the "categories" view
    }

    // Handle POST requests to "/categories" URL for adding or updating a category group
    @PostMapping("/categories")
    public String addOrUpdateCategory(CategoryGroup categoryGroup, @RequestParam("coverImageFile") MultipartFile coverImageFile, RedirectAttributes redirectAttributes) {
        try {
            // Add or update the category group with the provided cover image
            categoryGroupService.addCategoryGroup(categoryGroup, coverImageFile);

            // Check if the category already exists in the repository
            if(categoryRepo.findByName(categoryGroup.getName()) != null) {
                CommunityCategory communityCategory = categoryRepo.findByName(categoryGroup.getName());
                communityCategory.setDescription(categoryGroup.getDescription()); // Update description
                communityCategory.setName(categoryGroup.getName()); // Update name
                categoryRepo.save(communityCategory); // Save the updated community category
            } else {
                // Create and save a new community category if it doesn't exist
                CommunityCategory communityCategory = new CommunityCategory(categoryGroup.getName(), categoryGroup.getDescription(), "students");
                categoryRepo.save(communityCategory);
            }

            // Add a success message to redirect attributes
            redirectAttributes.addFlashAttribute("successMessage", "Category group added/updated successfully.");
        } catch (IOException e) {
            // Handle cover image upload failure
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to upload cover image. Please try again.");
            logger.severe("Error adding/updating category group: " + e.getMessage());
        } catch (Exception e) {
            // Handle general failure to add/update category group
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to add/update category group. Please try again.");
            logger.severe("Error adding/updating category group: " + e.getMessage());
        }
        return "redirect:/categories"; // Redirect to the categories page
    }

    // Handle POST requests to "/delete-category/{id}" URL for deleting a category group
    @PostMapping("/delete-category/{id}")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Find the community category by name and delete it from the repository
            CommunityCategory communityCategory = categoryRepo.findByName(categoryGroupService.getCategoryGroupById(id).getName());
            categoryRepo.delete(communityCategory);

            // Delete the category group by ID
            categoryGroupService.deleteCategoryGroup(id);

            // Add a success message to redirect attributes
            redirectAttributes.addFlashAttribute("successMessage", "Category group deleted successfully.");
        } catch (Exception e) {
            // Handle failure to delete category group
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete category group. Please try again.");
            logger.severe("Error deleting category group: " + e.getMessage());
        }
        return "redirect:/categories"; // Redirect to the categories page
    }

    // Handle GET requests to "/edit-category/{id}" URL for editing a category group
    @GetMapping("/edit-category/{id}")
    public String editCategory(@PathVariable Long id, Model model) {
        CategoryGroup categoryGroup = categoryGroupService.getCategoryGroupById(id); // Get the category group by ID
        model.addAttribute("categoryGroup", categoryGroup); // Add the category group to the model
        model.addAttribute("categories", categoryGroupService.getAllCategoryGroups()); // Add all category groups to the model
        return "Marketplace/categories"; // Return the "categories" view
    }
}
