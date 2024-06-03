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
public class CategoryPostsController {

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

    @GetMapping("/posts")
    public String getMethodName(@RequestParam("category_name") String param, Model model) {
        param = param.substring(0, 1).toUpperCase() + param.substring(1); // capitalise first letter of category name
        model.addAttribute("category_name", param);
        return "posts";
    }
    
    
}
