package webprogramming.csc1106.Controllers;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import webprogramming.csc1106.Entities.*;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@Controller
public class ForumController {

    // @Autowired
    // private CourseForumRepo courseForumRepo;
    @Autowired
    private ForumThreadRepo forumThreadRepo;
    @Autowired
    private ThreadReplyRepo threadReplyRepo;

    @GetMapping("/forum")
    public String getAllThreads(Model model) {
        // Date date = new Date(100);
        // Time time = new Time(1000);
        // ForumThread thread1 = new ForumThread(1, 1, "Poster Name", date, time, 0, "Example title", "Example content");
        // ForumThread thread2 = new ForumThread(2, 1, "Poster Name2", date, time, 0, "Example title again", "Example content again");
        // forumThreadRepo.save(thread1);
        // forumThreadRepo.save(thread2);
        // model.addAttribute("thread1", thread1);
        // model.addAttribute("thread2", thread2);

        Iterable<ForumThread> queriedThreads = forumThreadRepo.findAll();
        ArrayList<ForumThread> threads = new ArrayList<>();
        for (ForumThread t : queriedThreads) {
            threads.add(t);
        }
        
        model.addAttribute("threads", threads.toArray());
        return "forum"; 
    }

    @GetMapping("/forum/{id}")
    public String openThread(@PathVariable String id, Model model) {
        int threadId = Integer.parseInt(id); // Convert String to int

        Optional<ForumThread> thread = forumThreadRepo.findById(threadId); // Find thread by id
        ArrayList<ForumThread> threadSelected = new ArrayList<>();

        thread.ifPresent(t -> threadSelected.add(t));

        model.addAttribute("threadSelected", threadSelected.toArray());

        Iterable<ThreadReply> threadReply = threadReplyRepo.findByThreadID(threadId); // Find replies by id
        ArrayList<ThreadReply> replies = new ArrayList<>();
        for (ThreadReply r : threadReply) {
            replies.add(r);
        }

        model.addAttribute("replies", replies.toArray());

        return "forumthread";
    }
    
    @PostMapping("/forum")
    public String postMethodName(@RequestBody String entity) {
        //TODO: process POST request
        
        return "forum";
    }
    

}
