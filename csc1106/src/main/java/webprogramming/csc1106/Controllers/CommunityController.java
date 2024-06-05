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
        Object[] announcementCount = categoryCounts.get(0);
        Object[] questionsCount = categoryCounts.get(1);
        Object[] eventsCount = categoryCounts.get(2);
        model.addAttribute("announcementCount", announcementCount[1]);
        model.addAttribute("questionsCount", questionsCount[1]);
        model.addAttribute("eventsCount", eventsCount[1]);

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
