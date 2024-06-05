package webprogramming.csc1106.Controllers;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import webprogramming.csc1106.Entities.CommunityCategory;
import webprogramming.csc1106.Entities.Post;
import webprogramming.csc1106.Repositories.CategoryRepo;
import webprogramming.csc1106.Repositories.PostRepo;

import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CommunityNewPostController {

    @Autowired
    private PostRepo postRepo;
    @Autowired
    private CategoryRepo categoryRepo;

    @GetMapping("/community/new-post")
    public String getNewPostForm(@Param("category_name") String category_name, Model model) {
        model.addAttribute("category_name", category_name);
        model.addAttribute("newPost", new Post());
        return "Community/new-post"; 
    }

    @PostMapping("/community/new-post")
    public String postNewPost(@Param("category_name") String category_name, @ModelAttribute Post newPost) {
        java.util.Date date = new java.util.Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        newPost.setTimestamp(timestamp);
        newPost.setPosterName("Example Name"); // placeholder

        CommunityCategory category = categoryRepo.findByName(category_name);
        newPost.setCategory(category);
        
        postRepo.save(newPost);

        return "redirect:/community/" + category_name; 
    }
    
}
