package webprogramming.csc1106.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import webprogramming.csc1106.Entities.*;
import webprogramming.csc1106.Repositories.CategoryRepo;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class CommunityCategoryController {

    @Autowired
    private CategoryRepo categoryRepo;

    @GetMapping("/community/students/{category_id}")
    public String getCategoryPostsStudents(@PathVariable String category_id, Model model) {

        CommunityCategory category = categoryRepo.findById(Integer.parseInt(category_id)); // retrieve category object from db by name
        List<Post> posts = category.getPosts(); // get retrieved category's posts
        
        model.addAttribute("user_group", "students");
        model.addAttribute("category_name", category.getName()); // add category name to template model
        model.addAttribute("category_id", category_id); // used for new post later
        model.addAttribute("posts", posts); // add posts to template model

        return "Community/community-category";
    }

    @GetMapping("/community/announcements")
    public String getAnnouncements(Model model) {
        model.addAttribute("category_name", "Announcements");

        CommunityCategory category = categoryRepo.findByName("Announcements");
        List<Post> posts = category.getPosts();
        model.addAttribute("posts", posts);

        return "Community/community-category";
    }

    @GetMapping("/community/students")
    public String getStudents(Model model) {
        return "Community/community-students";
    }

    @GetMapping("/community/instructors")
    public String getInstructors(Model model) {
        return "Community/community-instructors";
    }
    
}
