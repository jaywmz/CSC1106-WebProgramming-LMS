package webprogramming.csc1106.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import webprogramming.csc1106.Entities.*;
import webprogramming.csc1106.Repositories.CategoryRepo;

import org.springframework.ui.Model;

@Controller
public class CommunityCategoryController {

    @Autowired
    private CategoryRepo categoryRepo;

    @GetMapping("/community/{category_name}")
    public String getMethodName(@PathVariable String category_name, Model model) {
        category_name = category_name.substring(0, 1).toUpperCase() + category_name.substring(1); // capitalise first letter of category name
        model.addAttribute("category_name", category_name);

        CommunityCategory category = categoryRepo.findByName(category_name);
        List<Post> posts = category.getPosts();
        model.addAttribute("posts", posts);

        return "community-category";
    }
    
}
