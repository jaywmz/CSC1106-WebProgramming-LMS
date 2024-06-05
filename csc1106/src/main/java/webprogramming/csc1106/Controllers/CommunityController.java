package webprogramming.csc1106.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import webprogramming.csc1106.Entities.*;
import webprogramming.csc1106.Repositories.PostRepo;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class CommunityController {

    @Autowired
    private PostRepo postRepo;
    
    @GetMapping("/community")
    public String getCommunityHome(@RequestParam(defaultValue = "1") int page, Model model) {
        Page<Post> posts = postRepo.findAllByOrderByTimestampDesc(PageRequest.of(page - 1, 10));

        model.addAttribute("posts", posts.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPage", posts.getTotalPages());

        List<Object[]> categoryCounts = postRepo.findCategoryCounts();
        String[] categories = {
            "announcementCount", 
            "questionsCount", 
            "eventsCount"
        };

        for (int i = 0; i < categoryCounts.size(); i++) {
            model.addAttribute(categoryCounts.get(i)[0].toString() + "Count", categoryCounts.get(i)[1]);
        }

        // TODO: add more counters for the other categories, once finalised

        return "Community/community-home";
    }
    
    @GetMapping("/community/search")
    public String getSearchResults(@RequestParam("key") String key, @RequestParam(defaultValue = "1") int page, Model model) {
        Page<Post> queriedPosts = postRepo.findAllByTitleContainingOrContentContainingOrderByTimestampDesc(key, key, PageRequest.of(page - 1, 5));
        
        model.addAttribute("posts", queriedPosts.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("search_term", key);
        model.addAttribute("totalPage", queriedPosts.getTotalPages());
        return "Community/community-search";
    }
}
