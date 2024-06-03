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
public class CategoryController {

    // @Autowired
    // private CourseForumRepo courseForumRepo;
    // @Autowired
    // private CategoryRepo forumThreadRepo;
    // @Autowired
    // private ThreadReplyRepo threadReplyRepo;

    // @GetMapping("/forum")
    // public String getAllThreads(@RequestParam(defaultValue = "1") int page, Model model) {
    //     Page<Post> queriedThreads = forumThreadRepo.findAllByForumIDOrderByPostDateDesc(1, PageRequest.of(page - 1, 10));

    //     model.addAttribute("threads", queriedThreads.getContent());
    //     model.addAttribute("currentPage", page);
    //     model.addAttribute("totalPage", queriedThreads.getTotalPages());
        
    //     return "forum";
    // }

    @GetMapping("/{category_name}")
    public String getMethodName(@PathVariable String category_name, Model model) {
        category_name = category_name.substring(0, 1).toUpperCase() + category_name.substring(1); // capitalise first letter of category name
        model.addAttribute("category_name", category_name);
        return "category";
    }
    
    
}
