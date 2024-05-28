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
public class ForumController {

    // @Autowired
    // private CourseForumRepo courseForumRepo;
    @Autowired
    private ForumThreadRepo forumThreadRepo;
    // @Autowired
    // private ThreadReplyRepo threadReplyRepo;

    @GetMapping("/forum")
    public String getAllThreads(@RequestParam(defaultValue = "1") int page, Model model) {
        Page<ForumThread> queriedThreads = forumThreadRepo.findAllByForumIDOrderByPostDateDesc(1, PageRequest.of(page - 1, 10));

        model.addAttribute("threads", queriedThreads.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPage", queriedThreads.getTotalPages());
        
        return "forum";
    }

}
