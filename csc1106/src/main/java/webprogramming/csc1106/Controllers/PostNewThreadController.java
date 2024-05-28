package webprogramming.csc1106.Controllers;

import java.sql.Date;
import java.sql.Time;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import webprogramming.csc1106.Entities.ForumThread;
import webprogramming.csc1106.Repositories.ForumThreadRepo;

import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PostNewThreadController {

    @Autowired
    private ForumThreadRepo forumThreadRepo;
    // @Autowired
    // private ThreadReplyRepo threadReplyRepo;

    @GetMapping("/post-thread")
    public String getNewThreadForm(Model model) {
        model.addAttribute("newThread", new ForumThread());
        return "post-thread"; 
    }

    @PostMapping("/post-thread")
    public String postNewThread(@ModelAttribute ForumThread newThread, Model model) {
        java.util.Date date = new java.util.Date();
        Date sqlDate = new Date(date.getTime());
        Time sqlTime = new Time(date.getTime());
        newThread.setPostDate(sqlDate);
        newThread.setPostTime(sqlTime);
        newThread.setPosterName("Example name"); // placeholder
        
        forumThreadRepo.save(newThread);
        
        // need to change so it actually redirects to the newly created thread's dedicated page
        return "redirect:forum"; 
    }
    
}
