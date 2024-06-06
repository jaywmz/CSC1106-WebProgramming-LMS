package webprogramming.csc1106.Controllers;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

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

    @GetMapping("/community/{user_group}/new-post")
    public String getNewPostForm(@PathVariable String user_group, @Param("category_id") String category_id, Model model) {
        CommunityCategory category = categoryRepo.findById(Integer.parseInt(category_id)); // retrieve category object from db by name

        model.addAttribute("user_group", user_group);
        model.addAttribute("category_name", category.getName());
        model.addAttribute("category_id", category_id);
        model.addAttribute("newPost", new Post());
        return "Community/new-post"; 
    }

    @PostMapping("/community/new-post")
    public String postNewPost(@Param("category_id") String category_id, @ModelAttribute Post newPost) {
        java.util.Date date = new java.util.Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        newPost.setTimestamp(timestamp);
        newPost.setPosterName("Example Name"); // placeholder

        CommunityCategory category = categoryRepo.findById(Integer.parseInt(category_id));
        newPost.setCategory(category);
        
        postRepo.save(newPost);

        if(Integer.parseInt(category_id) <= 2){
            return "redirect:/community/announcements";
        }else if(Integer.parseInt(category_id) <= 8){
            return "redirect:/community/students/" + category_id;
        }else if(Integer.parseInt(category_id) <= 10){
            return "redirect:/community/instructors/" + category_id;
        }else{
            return "redirect:/community/" + category_id; 
        }
    }
    
}
