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

    // Mapping for specific post page
    @GetMapping("/community/{user_group}/{category_id}/{post_id}")
    public String getPost(@PathVariable String user_group, @PathVariable String category_id, @PathVariable String post_id, @CookieValue(value="lrnznth_User_ID", required=false) String userID, Model model) {
        try {
            if(userID == null) {
                return "redirect:/login";
            }
            // retrieve models
            Optional<Post> post = postRepo.findByPostID(Long.parseLong(post_id));
            if (post.isEmpty()) {
                throw new Exception("Post specified does not exist");
            }
            String category_name = post.get().getCategory().getName();
            List<PostAttachments> attachments = post.get().getAttachments();
            Optional<User> user = userRepo.findById(Integer.parseInt(userID));
            Optional<Likes> like = Optional.empty();
            Optional<Subscription> sub = Optional.empty();
            if (user.isPresent()) {
                like = likesRepo.findById(new LikesID(user.get().getUserID(), post.get().getPostID()));
                sub = subsRepo.findById(new SubscribeID(user.get().getUserID(), post.get().getPostID()));
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
            model.addAttribute("post", post.get());
            model.addAttribute("category_name", category_name);
            model.addAttribute("user_group", user_group);
            model.addAttribute("newComment", new Comment());
            model.addAttribute("comments", post.get().getComments().reversed());
            if (like.isPresent()) model.addAttribute("liked", true); 
            else model.addAttribute("liked", false);
            if (sub.isPresent()) model.addAttribute("subbed", true); 
            else model.addAttribute("subbed", false);
            
            return "Community/post";
        } catch (Exception e) {
            System.err.println(e);
            return "redirect:/community/{user_group}/{category_id}";
        }
    }

    // Mapping for adding a comment to a post
    @PostMapping("/community/{user_group}/{category_id}/{post_id}/add-comment")
    public String postComment(@ModelAttribute Comment newComment, @PathVariable String user_group, @PathVariable String category_id, @PathVariable String post_id, @CookieValue("lrnznth_User_Name") String username, RedirectAttributes redirectAttributes) {
        try {
            // checks if comment exceeds word limit of 255
            if (newComment.getContent().length() > 255) {
                throw new Exception("Comment exceeds character limit");
            }

            // set comment's timestmap
            java.util.Date date = new java.util.Date();
            Timestamp timestamp = new Timestamp(date.getTime());
            newComment.setTimestamp(timestamp);
            newComment.setCommenterName(username);

            // retrieve the post entity that this new comment is under
            Optional<Post> post = postRepo.findByPostID(Long.parseLong(post_id));
            if (post.isEmpty()) { throw new Exception("Post specified does not exist"); }
            else { newComment.setPost(post.get()); }

            // save new comment to repo
            commentRepo.save(newComment);
            
            redirectAttributes.addFlashAttribute("successMessage", "Comment added successfully!");

            return "redirect:/community/{user_group}/{category_id}/{post_id}";
        } catch (Exception exception) {
            System.err.println(exception);
            return "redirect:/community/{user_group}/{category_id}/{post_id}";
        }
    }
    
    // Mapping for liking a post
    @PostMapping("/community/{user_group}/{category_id}/{post_id}/like")
    public ResponseEntity<String> likePost(@PathVariable String user_group, @PathVariable String category_id, @PathVariable String post_id, @CookieValue("lrnznth_User_ID") String userID) {
        try{
            // retrieve post object and increment like count
            Post likedPost = new Post();
            Optional<Post> post = postRepo.findByPostID(Long.parseLong(post_id));
            if (post.isEmpty()) { throw new Exception("Post specified does not exist"); }
            else { likedPost = post.get(); }
            likedPost.setLikes(likedPost.getLikes() + 1);
            
            // retrieve user object, create new like object and save to repo
            Optional<User> user = userRepo.findById(Integer.parseInt(userID));
            if (user.isPresent()) {
                LikesID likesID = new LikesID(user.get().getUserID(), likedPost.getPostID());
                Likes newLike = new Likes(likesID, user.get(), likedPost);
                likesRepo.save(newLike);
                postRepo.save(likedPost);
            }
            else { throw new Exception("User does not exist"); }

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            System.err.println(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    // Mapping for unliking a post
    @PostMapping("/community/{user_group}/{category_id}/{post_id}/unlike")
    public ResponseEntity<String> unlikePost(@PathVariable String user_group, @PathVariable String category_id, @PathVariable String post_id, @CookieValue("lrnznth_User_ID") String userID) {
        try{
            // retrieve post object and decrement like count
            Post unlikedPost = new Post();
            Optional<Post> post = postRepo.findByPostID(Long.parseLong(post_id));
            if (post.isEmpty()) { throw new Exception("Post specified does not exist"); }
            else { unlikedPost = post.get(); } 
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
            System.err.println(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Mapping for subscribing to a post
    @PostMapping("/community/{user_group}/{category_id}/{post_id}/subscribe")
    public ResponseEntity<String> subPost(@PathVariable String user_group, @PathVariable String category_id, @PathVariable String post_id, @CookieValue("lrnznth_User_ID") String userID) {
        try{
            // retrieve post object
            Post subbedPost = new Post();
            Optional<Post> post = postRepo.findByPostID(Long.parseLong(post_id));
            if (post.isEmpty()) { throw new Exception("Post specified does not exist"); }
            else { subbedPost = post.get(); } 

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
            System.err.println(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Mapping for unsubscribing to a post
    @PostMapping("/community/{user_group}/{category_id}/{post_id}/unsubscribe")
    public ResponseEntity<String> unsubPost(@PathVariable String user_group, @PathVariable String category_id, @PathVariable String post_id, @CookieValue("lrnznth_User_ID") String userID) {
        try{
            // retrieve post object
            Post unSubbedPost = new Post();
            Optional<Post> post = postRepo.findByPostID(Long.parseLong(post_id));
            if (post.isEmpty()) { throw new Exception("Post specified does not exist"); }
            else { unSubbedPost = post.get(); } 
            
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
            System.err.println(e);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Mapping for editing a post
    @PostMapping("/community/{user_group}/{category_id}/{post_id}/update-post")
    public String updatePost(@ModelAttribute Post post, @PathVariable String user_group, @PathVariable String category_id, @PathVariable String post_id, RedirectAttributes redirectAttributes) {
        try {
            // retrieve post entity by post id from path variable
            Post originalPost = new Post();
            Optional<Post> maybePost = postRepo.findByPostID(Long.parseLong(post_id));
            if (maybePost.isEmpty()) { throw new Exception("Specified post does not exist"); }
            else { originalPost = maybePost.get(); }

            // set post's content to the content in the post entity set in the View, the content is user-input.
            originalPost.setContent(post.getContent());

            // save modified post to repo
            postRepo.save(originalPost);

            redirectAttributes.addFlashAttribute("successMessage", "Post edited successfully!");
            
            return "redirect:/community/{user_group}/{category_id}/{post_id}";
        } catch (Exception e) {
            System.err.println(e);
            return "redirect:/community/{user_group}/{category_id}";
        }
    }

    // Mapping for deleting a post
    @PostMapping("/community/{user_group}/{category_id}/delete-post")
    public String deletePost(@PathVariable String user_group, @PathVariable String category_id, @RequestParam("postId") Long postId, RedirectAttributes redirectAttributes) {
        try {
            postRepo.deleteByPostID(postId.intValue());

            redirectAttributes.addFlashAttribute("successMessage", "Post deleted successfully!");
        
            return "redirect:/community/{user_group}/{category_id}";
        } catch (Exception e) {
            System.err.println(e);
            return "redirect:/community/{user_group}/{category_id}";
        }
    }

}   
