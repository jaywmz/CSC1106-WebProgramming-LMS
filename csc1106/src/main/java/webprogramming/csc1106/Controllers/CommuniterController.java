package webprogramming.csc1106.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import webprogramming.csc1106.Entities.*;
import webprogramming.csc1106.Repositories.ForumThreadRepo;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class CommuniterController {
    
    @GetMapping("/community")
    public String getCommunityHome() {
        return "community-home";
    }
    
}
