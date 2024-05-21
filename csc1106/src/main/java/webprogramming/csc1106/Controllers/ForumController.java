package webprogramming.csc1106.Controllers;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import webprogramming.csc1106.Entities.*;
import webprogramming.csc1106.Repositories.ForumThreadRepo;
import webprogramming.csc1106.Repositories.ThreadReplyRepo;

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
    public String getAllThreads(@RequestParam(defaultValue = "1") int page, Model model) {
        // TODO: change the forumID parameter to the user selected parameter
        Page<ForumThread> queriedThreads = forumThreadRepo.findAllByForumID(1, PageRequest.of(page - 1, 10));

        model.addAttribute("threads", queriedThreads.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPage", queriedThreads.getTotalPages());
        
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
        // System.out.println(replies.toArray());
        model.addAttribute("newReply", new ThreadReply());

        return "forumthread";
    }

}
