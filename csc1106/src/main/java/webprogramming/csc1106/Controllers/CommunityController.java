package webprogramming.csc1106.Controllers;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import webprogramming.csc1106.Entities.*;
import webprogramming.csc1106.Repositories.CategoryRepo;
import webprogramming.csc1106.Repositories.PostRepo;
import webprogramming.csc1106.Repositories.SubscriptionsRepo;
import webprogramming.csc1106.Repositories.UserRepository;

@Controller
public class CommunityController {
    @Autowired
    private PostRepo postRepo;
    @Autowired
    private SubscriptionsRepo subRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private CategoryRepo categoryRepo;
    
    @GetMapping("/community")
    public String getCommunityHome(Model model, @CookieValue("lrnznth_User_ID") String userID) {
        // queries top 5 latest posts from repo
        List<Post> posts = postRepo.findTop5ByOrderByTimestampDesc();

        // queries 'general' categories
        List<CommunityCategory> categories = categoryRepo.findByGroup("general");

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
        model.addAttribute("categories", categories);

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

    @GetMapping("/community/unauthorised")
    public String getMethodName() {
        return "Community/community-unauthorised";
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
            Long totalIntroductions = 0L;
            Long totalCareers = 0L;
            
            for (int i = 0; i < categoryCounts.size(); i++) 
            {
                if("announcements".equals(categoryCounts.get(i)[1])) {
                    totalAnnouncements += (Long) categoryCounts.get(i)[3];
                } else if("students".equals(categoryCounts.get(i)[1])) {
                    totalStudents += (Long) categoryCounts.get(i)[3];
                } else if("instructors".equals(categoryCounts.get(i)[1])) {
                    totalInstructors += (Long) categoryCounts.get(i)[3];
                } else if(categoryCounts.get(i)[2].equals("Off-Topic")) {
                    totalOffTopic += (Long) categoryCounts.get(i)[3];
                } else if(categoryCounts.get(i)[2].equals("Feedback")) {
                    totalFeedback += (Long) categoryCounts.get(i)[3];
                } else if(categoryCounts.get(i)[2].equals("Introductions")) { 
                    totalIntroductions += (Long) categoryCounts.get(i)[3];
                } else if(categoryCounts.get(i)[2].equals("Careers")) {
                    totalCareers += (Long) categoryCounts.get(i)[3];
                } 
            }
            
            Object lastOffTopic = 0;
            if(totalOffTopic > 0){
                lastOffTopic = categoryCounts.get(2)[4];
            }
            Object lastFeedback = 0;
            if(totalFeedback > 0){
                lastFeedback = categoryCounts.get(3)[4];
            }
            Object lastIntroductions = 0;
            if(totalIntroductions > 0){
                lastIntroductions = categoryCounts.get(9)[4];
            }
            Object lastCareers = 0;
            if(totalCareers > 0){
                lastCareers = categoryCounts.get(10)[4];
            }
            
            return new ResponseEntity<>(List.of(
                totalAnnouncements, 
                totalStudents, 
                totalInstructors, 
                totalOffTopic, 
                totalFeedback,
                totalIntroductions,
                totalCareers,
                lastOffTopic,
                lastFeedback,
                lastIntroductions,
                lastCareers
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
            Dictionary<Long, Long> countsDict = new Hashtable<>(); 

            for(int i = 0; i < categoryCounts.size(); i++){
                countsDict.put( (Long) categoryCounts.get(i)[0], (Long) categoryCounts.get(i)[2] );
            }

            return new ResponseEntity<>(List.of(
                countsDict
                ), HttpStatus.OK);
        }catch(Exception e){
            System.out.println(e.getMessage());
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
            System.out.println(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/community-get-user-role")
    @ResponseBody
    public String getUserRole(@CookieValue("lrnznth_User_ID") String userID) {

        User user = userRepo.findByUserId(Integer.parseInt(userID));
        Roles role = user.getRole();
        int roleId = role.getRoleID();
        String instructorCheck;

        if(roleId == 3){
            instructorCheck = "no";
        }else{
            instructorCheck = "yes";
        }

        return instructorCheck;
    }
    
}
