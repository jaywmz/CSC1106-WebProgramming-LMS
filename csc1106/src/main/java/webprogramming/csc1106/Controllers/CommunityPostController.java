package webprogramming.csc1106.Controllers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import webprogramming.csc1106.Entities.Comment;
import webprogramming.csc1106.Entities.Post;
import webprogramming.csc1106.Entities.PostAttachments;
import webprogramming.csc1106.Repositories.CategoryRepo;
import webprogramming.csc1106.Repositories.CommentRepo;
import webprogramming.csc1106.Repositories.PostRepo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;




@Controller
public class CommunityPostController {
    @Autowired
    private PostRepo postRepo;
    @Autowired
    private CommentRepo commentRepo;

    @GetMapping("/community/{user_group}/{category_id}/{post_id}")
    public String getPost(@PathVariable String category_id, @PathVariable String post_id, @PathVariable String user_group, Model model) {
        Long post_ID = Long.parseLong(post_id);
        Post post = postRepo.findById(post_ID);
        String category_name = post.getCategory().getName();
        
        List<PostAttachments> attachments = post.getAttachments();
        ArrayList<String> urls = new ArrayList<String>();
        if (!attachments.isEmpty()) {
            for (PostAttachments attachment : attachments) {
                urls.add(attachment.getURI());
            }
            model.addAttribute("attachment_urls", urls);
        }
        else {
            model.addAttribute("attach_urls", new String[0]);
        }

        model.addAttribute("post", post);
        model.addAttribute("category_name", category_name);
        model.addAttribute("user_group", user_group);
        model.addAttribute("newComment", new Comment());
        model.addAttribute("comments", post.getComments().reversed());

        return "Community/post";
    }

    @PostMapping("/community/{user_group}/{category_id}/{post_id}/add-comment")
    public String postComment(@ModelAttribute Comment newComment, @PathVariable String user_group, @PathVariable String category_id, @PathVariable String post_id) {
        java.util.Date date = new java.util.Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        newComment.setTimestamp(timestamp);
        newComment.setCommenterName("Bozo");

        Post post = postRepo.findById(Long.parseLong(post_id));
        // post.getComments().add(newComment);
        newComment.setPost(post);

        // postRepo.save(post);
        commentRepo.save(newComment);
        
        return "redirect:/community/{user_group}/{category_id}/{post_id}";
    }
    
    @PostMapping("/community/{user_group}/{category_id}/{post_id}/like")
    public ResponseEntity<String> likePost(@PathVariable String user_group, @PathVariable String category_id, @PathVariable String post_id) {
        try{
            Post likedPost = postRepo.findById(Long.parseLong(post_id));
            likedPost.setLikes(likedPost.getLikes() + 1);
            postRepo.save(likedPost);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
    
    @PostMapping("/community/{user_group}/{category_id}/{post_id}/unlike")
    public ResponseEntity<String> unlikePost(@PathVariable String user_group, @PathVariable String category_id, @PathVariable String post_id) {
        try{
            Post unlikedPost = postRepo.findById(Long.parseLong(post_id));
            unlikedPost.setLikes(unlikedPost.getLikes() - 1);
            postRepo.save(unlikedPost);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}   
