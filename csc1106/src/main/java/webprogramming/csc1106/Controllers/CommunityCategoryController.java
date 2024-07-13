package webprogramming.csc1106.Controllers;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import webprogramming.csc1106.Entities.*;
import webprogramming.csc1106.Repositories.CategoryRepo;
import webprogramming.csc1106.Repositories.PostRepo;

import org.springframework.ui.Model;


@Controller
public class CommunityCategoryController {
    @Autowired
    private CategoryRepo categoryRepo;
    @Autowired
    private PostRepo postRepo;

    // Method for returning views of general categories that are not within a group, such as Off-topic and Feedback
    @GetMapping("/community/general/{categoryName}")
    public String getCategoryPosts(@PathVariable String categoryName, Model model) {
        CommunityCategory category = categoryRepo.findByNameIgnoreCase(categoryName); // retrieve category object from db by name
        List<Post> posts = category.getPosts(); // get retrieved category's posts
        
        model.addAttribute("user_group", "general");
        model.addAttribute("category_name", category.getName()); // add category name to template model
        model.addAttribute("category_id", category.getId()); // used for new post later
        model.addAttribute("posts", posts); // add posts to template model

        return "Community/community-category";
    }

    // Method for returning views of categories within a group, such as Announcement, Student, Instructor
    @GetMapping("/community/{user_group}/{category_id}")
    public String getCategoryPosts(@PathVariable String user_group, @PathVariable String category_id, Model model) {
        CommunityCategory category = categoryRepo.findById(Integer.parseInt(category_id)); // retrieve category object from db by name
        List<Post> posts = category.getPosts(); // get retrieved category's posts
        
        model.addAttribute("user_group", user_group);
        model.addAttribute("category_name", category.getName()); // add category name to template model
        model.addAttribute("category_id", category_id); // used for new post later
        model.addAttribute("posts", posts); // add posts to template model

        return "Community/community-category";
    }

    @GetMapping("/community/announcements")
    public String getAnnouncements(Model model) {
        List<Integer> categoryIds = Arrays.asList(1, 2);
        List<Post> posts = postRepo.findTop5ByCategoryIdInOrderByTimestampDesc(categoryIds);

        model.addAttribute("posts", posts);
        return "Community/community-announce";
    }

    @GetMapping("/community/students")
    public String getStudents(Model model) {
        // get all the categories under student group
        List<CommunityCategory> categories = categoryRepo.findByGroup("students");

        // TO-DO: get recent posts from categories under student group and put in posts

        // model.addAttribute("posts", post);
        model.addAttribute("categories", categories);
        return "Community/community-students";
    }

    @GetMapping("/community/instructors")
    public String getInstructors(Model model, @CookieValue("lrnznth_User_ID") String userID) {
        // TO-DO: get recent posts from categories under instructor group and put in posts
        
        // model.addAttribute("posts", posts);
        return "Community/community-instructors";
    }
    
}
