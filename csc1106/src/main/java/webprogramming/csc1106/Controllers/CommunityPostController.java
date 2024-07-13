package webprogramming.csc1106.Controllers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import webprogramming.csc1106.Entities.*;
import webprogramming.csc1106.Models.LikesID;
import webprogramming.csc1106.Models.SubscribeID;
import webprogramming.csc1106.Repositories.*;

@Controller
public class CommunityPostController {
    @Autowired
    private PostRepo postRepo;
    @Autowired
    private CommentRepo commentRepo;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private LikesRepo likesRepo;
    @Autowired
    private SubscriptionsRepo subsRepo;
    // @Autowired 
    // private AttachmentsRepo attachmentsRepo;

    @GetMapping("/community/{user_group}/{category_id}/{post_id}")
    public String getPost(@PathVariable String user_group, @PathVariable String category_id, @PathVariable String post_id, @CookieValue(value="lrnznth_User_ID", required=false) String userID, Model model) {
        if(userID == null) {
            return "redirect:/login";
        }
        // retrieve models
        Post post = postRepo.findByPostID(Long.parseLong(post_id));
        String category_name = post.getCategory().getName();
        List<PostAttachments> attachments = post.getAttachments();
        Optional<User> user = userRepo.findById(Integer.parseInt(userID));
        Optional<Likes> like = Optional.empty();
        Optional<Subscription> sub = Optional.empty();
        if (user.isPresent()) {
            like = likesRepo.findById(new LikesID(user.get().getUserID(), post.getPostID()));
            sub = subsRepo.findById(new SubscribeID(user.get().getUserID(), post.getPostID()));
        }

        // extract urls from attachments list
        ArrayList<String> urls = new ArrayList<String>();
        if (!attachments.isEmpty()) {
            for (PostAttachments attachment : attachments) {
                urls.add(attachment.getURI());
            }
            model.addAttribute("attachment_urls", urls);
        }
        else {
            model.addAttribute("attach_urls", new String[0]); // make empty arraylist if no attachments retrieved
        }

        // add attributes to thymeleaf model
        model.addAttribute("post", post);
        model.addAttribute("category_name", category_name);
        model.addAttribute("user_group", user_group);
        model.addAttribute("newComment", new Comment());
        model.addAttribute("comments", post.getComments().reversed());
        if (like.isPresent()) model.addAttribute("liked", true); 
        else model.addAttribute("liked", false);
        if (sub.isPresent()) model.addAttribute("subbed", true); 
        else model.addAttribute("subbed", false);
        
        return "Community/post";
    }

    @PostMapping("/community/{user_group}/{category_id}/{post_id}/add-comment")
    public String postComment(@ModelAttribute Comment newComment, @PathVariable String user_group, @PathVariable String category_id, @PathVariable String post_id, @CookieValue("lrnznth_User_Name") String username, RedirectAttributes redirectAttributes) {
        java.util.Date date = new java.util.Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        newComment.setTimestamp(timestamp);
        newComment.setCommenterName(username);

        Post post = postRepo.findByPostID(Long.parseLong(post_id));
        // post.getComments().add(newComment);
        newComment.setPost(post);

        // postRepo.save(post);
        commentRepo.save(newComment);
        
        redirectAttributes.addFlashAttribute("successMessage", "Comment added successfully!");


        return "redirect:/community/{user_group}/{category_id}/{post_id}";
    }
    
    @PostMapping("/community/{user_group}/{category_id}/{post_id}/like")
    public ResponseEntity<String> likePost(@PathVariable String user_group, @PathVariable String category_id, @PathVariable String post_id, @CookieValue("lrnznth_User_ID") String userID) {
        try{
            // retrieve post object and increment like count
            Post likedPost = postRepo.findByPostID(Long.parseLong(post_id));
            likedPost.setLikes(likedPost.getLikes() + 1);
            
            // retrieve user object, create new like object and save to repo
            Optional<User> user = userRepo.findById(Integer.parseInt(userID));
            if (user.isPresent()) {
                LikesID likesID = new LikesID(user.get().getUserID(), likedPost.getPostID());
                Likes newLike = new Likes(likesID, user.get(), likedPost);
                likesRepo.save(newLike);
                postRepo.save(likedPost);
            }
            else {
                throw new Exception("User does not exist");
            }

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping("/community/{user_group}/{category_id}/{post_id}/unlike")
    public ResponseEntity<String> unlikePost(@PathVariable String user_group, @PathVariable String category_id, @PathVariable String post_id, @CookieValue("lrnznth_User_ID") String userID) {
        try{
            // retrieve post object and decrement like count
            Post unlikedPost = postRepo.findByPostID(Long.parseLong(post_id));
            unlikedPost.setLikes(unlikedPost.getLikes() - 1);
            postRepo.save(unlikedPost);

            // retrieve user object, create likesID object and delete from repo by likesID
            Optional<User> user = userRepo.findById(Integer.parseInt(userID));
            if (user.isPresent()) {
                LikesID likeID = new LikesID(user.get().getUserID(), unlikedPost.getPostID());
                likesRepo.deleteById(likeID);
            }
            else {
                throw new Exception("User does not exist");
            }

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/community/{user_group}/{category_id}/{post_id}/subscribe")
    public ResponseEntity<String> subPost(@PathVariable String user_group, @PathVariable String category_id, @PathVariable String post_id, @CookieValue("lrnznth_User_ID") String userID) {
        try{
            // retrieve post object
            Post subbedPost = postRepo.findByPostID(Long.parseLong(post_id));
            
            // retrieve user object 
            Optional<User> user = userRepo.findById(Integer.parseInt(userID));
            
            // create new subscription object
            if (user.isPresent()) {
                SubscribeID subID = new SubscribeID(user.get().getUserID(), subbedPost.getPostID());
                Subscription newSub = new Subscription(subID, user.get(), subbedPost);
                // save to repo
                subsRepo.save(newSub);
            }
            else {
                throw new Exception("User does not exist");
            }

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/community/{user_group}/{category_id}/{post_id}/unsubscribe")
    public ResponseEntity<String> unsubPost(@PathVariable String user_group, @PathVariable String category_id, @PathVariable String post_id, @CookieValue("lrnznth_User_ID") String userID) {
        try{
            // retrieve post object
            Post unSubbedPost = postRepo.findByPostID(Long.parseLong(post_id));
            
            // retrieve user object by username
            Optional<User> user = userRepo.findById(Integer.parseInt(userID));

            // make subcription ID, the key to subscription table
            if (user.isPresent()) {
                SubscribeID subID = new SubscribeID(user.get().getUserID(), unSubbedPost.getPostID());
                // remove subscription from subcription repo by subID
                subsRepo.deleteById(subID);
            }
            else {
                throw new Exception("User does not exist");
            }

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/community/{user_group}/{category_id}/{post_id}/update-post")
    public String updatePost(@ModelAttribute Post post, @PathVariable String user_group, @PathVariable String category_id, @PathVariable String post_id, RedirectAttributes redirectAttributes) {
        Post originalPost = postRepo.findByPostID(Long.parseLong(post_id));
        originalPost.setContent(post.getContent());
        postRepo.save(originalPost);

        redirectAttributes.addFlashAttribute("successMessage", "Post edited successfully!");
        
        return "redirect:/community/{user_group}/{category_id}/{post_id}";
    }

    @PostMapping("/community/{user_group}/{category_id}/delete-post")
    public String deletePost(@PathVariable String user_group, @PathVariable String category_id, @RequestParam("postId") Long postId, RedirectAttributes redirectAttributes) {

        postRepo.deleteByPostID(postId.intValue());

        redirectAttributes.addFlashAttribute("successMessage", "Post deleted successfully!");
        
        return "redirect:/community/{user_group}/{category_id}";
    }

}   
