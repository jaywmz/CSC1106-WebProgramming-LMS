package webprogramming.csc1106.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import webprogramming.csc1106.Entities.*;
import webprogramming.csc1106.Repositories.CategoryRepo;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class CommunityController {
    
    @GetMapping("/community")
    public String getCommunityHome() {
        return "community-home";
    }
    
    // @GetMapping("/community/{category_name}")
    // public String getCategory(@PathVariable String category_name) {
    //     return "redirect:/" + category_name;
    // }
}
