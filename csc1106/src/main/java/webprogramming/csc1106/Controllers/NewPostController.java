package webprogramming.csc1106.Controllers;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import webprogramming.csc1106.Entities.Category;
import webprogramming.csc1106.Entities.Post;
import webprogramming.csc1106.Repositories.CategoryRepo;
import webprogramming.csc1106.Repositories.PostRepo;

import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class NewPostController {

    @Autowired
    private PostRepo postRepo;
    @Autowired
    private CategoryRepo categoryRepo;

    @GetMapping("/new-post")
    public String getNewPostForm(@Param("category_name") String category_name, Model model) {
        model.addAttribute("category_name", category_name);
        model.addAttribute("newPost", new Post());
        return "new-post"; 
    }

    @PostMapping("/new-post")
    public String postNewPost(@Param("category_name") String category_name, @ModelAttribute Post newPost) {
        java.util.Date date = new java.util.Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        newPost.setTimestamp(timestamp);
        newPost.setPosterName("Example Name"); // placeholder

        Category category = categoryRepo.findByName(category_name).getFirst();
        newPost.setCategory(category);
        
        postRepo.save(newPost);

        // need to change so it actually redirects to the newly created thread's dedicated page
        return "redirect:/" + category_name; 
    }
    
}
