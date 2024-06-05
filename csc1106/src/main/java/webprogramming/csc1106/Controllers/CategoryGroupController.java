package webprogramming.csc1106.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import webprogramming.csc1106.Entities.CategoryGroup;
import webprogramming.csc1106.Services.CategoryGroupService;

@Controller
public class CategoryGroupController {

    @Autowired
    private CategoryGroupService categoryGroupService;

    @GetMapping("/add-category")
    public String showAddCategoryPage(Model model) {
        model.addAttribute("categoryGroup", new CategoryGroup());
        return "Marketplace/add-category";
    }

    @PostMapping("/add-category")
    public String addCategory(CategoryGroup categoryGroup, Model model, RedirectAttributes redirectAttributes) {
        try {
            categoryGroupService.addCategoryGroup(categoryGroup);
            redirectAttributes.addFlashAttribute("successMessage", "Category group added successfully.");
            return "redirect:/add-category";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Failed to add category group. Please try again.");
            return "Marketplace/add-category";
        }
    }
}
