package webprogramming.csc1106.Controllers;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import webprogramming.csc1106.Entities.Post;
import webprogramming.csc1106.Entities.Comment;
import webprogramming.csc1106.Repositories.CategoryRepo;
import webprogramming.csc1106.Repositories.CommentRepo;

@Controller
public class ThreadController {
    
    // @Autowired
    // private CategoryRepo forumThreadRepo;

    // @Autowired
    // private CommentRepo threadReplyRepo;

    // @GetMapping("/forum/{id}")
    // public String openThread(@PathVariable String id, Model model) {
    //     int threadId = Integer.parseInt(id); // Convert String to int

    //     Optional<Post> thread = forumThreadRepo.findById(threadId); // Find thread by id
    //     ArrayList<Post> threadSelected = new ArrayList<>();

    //     thread.ifPresent(t -> threadSelected.add(t));

    //     model.addAttribute("threadSelected", threadSelected.toArray());

    //     Iterable<Comment> threadReply = threadReplyRepo.findByThreadIDOrderByReplyDateDescReplyTimeDesc(threadId); // Find replies by id
    //     ArrayList<Comment> replies = new ArrayList<>();
    //     ArrayList<Comment> replyReplies = new ArrayList<>();
    //     for (Comment r : threadReply) {
    //         if (r.getCommentID() == 0){
    //             replies.add(r);
    //         }else{
    //             replyReplies.add(r);
    //         }
    //     }

    //     model.addAttribute("replies", replies.toArray());
    //     model.addAttribute("replyReplies", replyReplies.toArray());
    //     // System.out.println(replies.toArray());
    //     model.addAttribute("newReply", new Comment());

    //     return "thread";
    // }

    // @PostMapping("/add-reply/{commentID}/{threadID}")
    // public String addReply(@ModelAttribute Comment newReply, Model model, @PathVariable String commentID, @PathVariable String threadID){
    //     java.util.Date date = new java.util.Date();
    //     Date sqlDate = new Date(date.getTime());
    //     Time sqlTime = new Time(date.getTime());
    //     newReply.setReplyDate(sqlDate);
    //     newReply.setReplyTime(sqlTime);
    //     newReply.setResponderName("Example name"); // placeholder
    //     newReply.setCommentID(Integer.parseInt(commentID));
    //     newReply.setThreadID(Integer.parseInt(threadID));

    //     threadReplyRepo.save(newReply);

    //     // need to change so it actually redirects to the newly created thread's dedicated page
    //     return "redirect:/forum/{threadID}"; 
    // }

}
