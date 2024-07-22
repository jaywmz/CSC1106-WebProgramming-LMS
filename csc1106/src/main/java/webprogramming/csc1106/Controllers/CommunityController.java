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
    
    // Mapping for community home page
    @GetMapping("/community/home")
    public String getCommunityHome(Model model, @CookieValue(value="lrnznth_User_ID", required = false) String userID) {
        // check if cookie contains userID
        if(userID == null) {
            return "redirect:/login";
        }

        // queries top 5 latest posts from repo
        List<Post> posts = postRepo.findTop5ByOrderByTimestampDesc();

        // retrieves category entities under the "general" group from category repo
        List<CommunityCategory> categories = categoryRepo.findByGroup("general");

        // retrieves user entity given user id in cookie
        Optional<User> user = userRepo.findById(Integer.parseInt(userID));
            
        // get list of posts that user has subscribed to
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

        // add posts and cateogories to view's model
        model.addAttribute("posts", posts);
        model.addAttribute("categories", categories);

        return "Community/community-home";
    }

    // Mapping for community loading page before home page
    @GetMapping("/community")
    public String getCommunityLoading() {
        return "Community/community-loading";
    }
    
    // Mapping for community search page
    @GetMapping("/community/search")
    public String getSearchResults(@RequestParam("key") String key, @RequestParam(defaultValue = "1") int page, Model model, @CookieValue(value="lrnznth_User_ID", required = false) String userID) {
        // check if cookie contains userID
        if(userID == null) {
            return "redirect:/login";
        }

        // retrieves posts containing the search term specified by user, ordered by time posted descendingly
        Page<Post> queriedPosts = postRepo.findAllByTitleContainingOrContentContainingOrderByTimestampDesc(key, key, PageRequest.of(page - 1, 5));

        model.addAttribute("posts", queriedPosts.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("search_term", key);
        model.addAttribute("totalPage", queriedPosts.getTotalPages());

        return "Community/community-search";
    }

    // Mapping for unauthorized page, for when student tries to access instructor's section of community hub
    @GetMapping("/community/unauthorised")
    public String getUnauthorisedPage() {
        return "Community/community-unauthorised";
    }

    // API to find and return sum of posts grouped by their categories
    @SuppressWarnings({ "null", "rawtypes" })
    @PostMapping("/community-get-post-count")
    public ResponseEntity<List> getPostsCount(){
        try{

            // queries category counts from repo
            List<Object[]> categoryCounts = postRepo.findCategoryCounts();
            
            Long totalAnnouncements = 0L;
            Long totalStudents = 0L;
            Long totalInstructors = 0L;
            Long totalOffTopic = 0L;
            Long totalFeedback = 0L;
            Long totalIntroductions = 0L;
            Long totalCareers = 0L;
            
            // loop through category counts and assigns sums to their respestive local variables
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
            
            // get time stamps of last posts of each category, assign to respective local variables
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
                lastIntroductions = categoryCounts.get(6)[4];
            }
            Object lastCareers = 0;
            if(totalCareers > 0){
                lastCareers = categoryCounts.get(7)[4];
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

    // API for getting sum of posts by category in students page
    @SuppressWarnings({ "null", "rawtypes" })
    @PostMapping("/community-get-post-count-students")
    public ResponseEntity<List> getPostsCountStudents(){
        try{

            // queries category counts from repo
            List<Object[]> categoryCounts = postRepo.findCategoryCountsStudents();
            Dictionary<Long, Long> countsDict = new Hashtable<>(); 

            // loop through category counts and assign to dictionary
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

    // API for getting sum of posts by category in instructors page
    @SuppressWarnings({ "null", "rawtypes" })
    @PostMapping("/community-get-post-count-instructors")
    public ResponseEntity<List> getPostsCountinstructors(){
        try{

            // queries category counts from repo
            List<Object[]> categoryCounts = postRepo.findCategoryCountsInstructors();
            
            // assign category counts to respective variables
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

    // API for getting user role
    @PostMapping("/community-get-user-role")
    @ResponseBody
    public int getUserRole(@CookieValue("lrnznth_User_ID") String userID) {

        User user = userRepo.findByUserId(Integer.parseInt(userID));
        Roles role = user.getRole();
        int roleId = role.getRoleID();

        return roleId;
    }
    
}
