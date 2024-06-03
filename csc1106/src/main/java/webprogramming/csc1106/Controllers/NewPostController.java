package webprogramming.csc1106.Controllers;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import webprogramming.csc1106.Entities.Post;
import webprogramming.csc1106.Repositories.CategoryRepo;

import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class NewPostController {

    // @Autowired
    // private CategoryRepo categoryRepo;
    // @Autowired
    // private ThreadReplyRepo threadReplyRepo;

    @GetMapping("/new-post")
    public String getNewThreadForm(Model model) {
        model.addAttribute("newPost", new Post());
        return "new-post"; 
    }

    @PostMapping("/new-post")
    public String postNewThread(@ModelAttribute Post newPost, Model model) {
        java.util.Date date = new java.util.Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        newPost.setTimestamp(timestamp);
        newPost.setPosterName("Example Name"); // placeholder
        
        // categoryRepo.save(newPost);
        
        // need to change so it actually redirects to the newly created thread's dedicated page
        return "redirect:forum"; 
    }
    
}
