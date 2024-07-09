package webprogramming.csc1106.Controllers;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.wavefront.WavefrontProperties.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.HttpStatus;


import webprogramming.csc1106.Entities.*;
import webprogramming.csc1106.Models.SubscribeID;
import webprogramming.csc1106.Repositories.PostRepo;
import webprogramming.csc1106.Repositories.SubscriptionsRepo;
import webprogramming.csc1106.Repositories.UserRepository;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class CommunityController {
    @Autowired
    private PostRepo postRepo;
    @Autowired
    private SubscriptionsRepo subRepo;
    @Autowired
    private UserRepository userRepo;
    
    @GetMapping("/community")
    public String getCommunityHome(Model model, @CookieValue("userId") String userID) {
        // queries top 5 latest posts from repo
        List<Post> posts = postRepo.findTop5ByOrderByTimestampDesc();

        // identify user
        Optional<User> user = userRepo.findById(Integer.parseInt(userID));

        // get list of subscribed posts from user 
        if (user.isPresent()) {
            List<Subscription> allSubscriptions = subRepo.findAllByUser(user.get());
            List<Long> postIDs = new ArrayList<Long>();
            for (Subscription sub : allSubscriptions) {
                postIDs.add(sub.getPost().getPostID());
            }
            List<Post> subbedPosts = postRepo.findAllById(postIDs);
            model.addAttribute("subbedPosts", subbedPosts);
        }
        else {
            model.addAttribute("subbedPosts", null);
        }

        model.addAttribute("posts", posts);

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

    @SuppressWarnings({ "null", "rawtypes" })
    @PostMapping("/community-get-post-count")
    public ResponseEntity<List> getPostsCount(){
        try{

            List<Object[]> categoryCounts = postRepo.findCategoryCounts();
            
            Long totalAnnouncements = 0L;
            Long totalStudents = 0L;
            Long totalInstructors = 0L;
            Long totalOffTopic = 0L;
            Long totalFeedback = 0L;
            
            for (int i = 0; i < categoryCounts.size(); i++) 
            {
                if("announcements".equals(categoryCounts.get(i)[1])) {
                    totalAnnouncements += (Long) categoryCounts.get(i)[2];
                } else if("students".equals(categoryCounts.get(i)[1])) {
                    totalStudents += (Long) categoryCounts.get(i)[2];
                } else if("instructors".equals(categoryCounts.get(i)[1])) {
                    totalInstructors += (Long) categoryCounts.get(i)[2];
                } else if(categoryCounts.get(i)[0].equals(11L)) { // 10 is index of off-topic category
                    totalOffTopic += (Long) categoryCounts.get(i)[2];
                } else if(categoryCounts.get(i)[0].equals(12L)) { // 11 is index of feedback category 
                    totalFeedback += (Long) categoryCounts.get(i)[2];
                }
            }
            
            Object lastOffTopic = 0;
            if(totalOffTopic > 0){
                lastOffTopic = categoryCounts.get(10)[3];
            }
            Object lastFeeback = 0;
            if(totalFeedback > 0){
                lastFeeback = categoryCounts.get(11)[3];
            }
            
            return new ResponseEntity<>(List.of(
                totalAnnouncements, 
                totalStudents, 
                totalInstructors, 
                totalOffTopic, 
                totalFeedback,
                lastOffTopic,
                lastFeeback
                ), HttpStatus.OK);
        }catch(Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @SuppressWarnings({ "null", "rawtypes" })
    @PostMapping("/community-get-post-count-students")
    public ResponseEntity<List> getPostsCountStudents(){
        try{

            List<Object[]> categoryCounts = postRepo.findCategoryCountsStudents();
            
            Long totalGeneral = (Long) categoryCounts.get(1)[2];
            Long totalItSoftware = (Long) categoryCounts.get(2)[2];
            Long totalBusiness = (Long) categoryCounts.get(3)[2];
            Long totalFinance = (Long) categoryCounts.get(4)[2];
            Long totalIntroductions = (Long) categoryCounts.get(0)[2];
            Long totalCareers = (Long) categoryCounts.get(5)[2];

            Object lastIntro = 0;
            Object lastCareers = 0;

            if(totalIntroductions > 0){
                lastIntro = categoryCounts.get(0)[3];
            }

            if(totalCareers > 0){
                lastCareers = categoryCounts.get(5)[3];
            }

            return new ResponseEntity<>(List.of(
                totalGeneral, 
                totalItSoftware, 
                totalBusiness, 
                totalFinance, 
                totalIntroductions, 
                totalCareers,
                lastIntro,
                lastCareers
                ), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @SuppressWarnings({ "null", "rawtypes" })
    @PostMapping("/community-get-post-count-instructors")
    public ResponseEntity<List> getPostsCountinstructors(){
        try{

            List<Object[]> categoryCounts = postRepo.findCategoryCountsInstructors();
            
            Long totalCourseHelp = (Long) categoryCounts.get(0)[2];
            Long totalTeaching = (Long) categoryCounts.get(1)[2];
            
            
            return new ResponseEntity<>(List.of(
                totalCourseHelp, totalTeaching
                ), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
}
