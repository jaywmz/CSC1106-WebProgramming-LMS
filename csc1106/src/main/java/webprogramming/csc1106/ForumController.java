package webprogramming.csc1106;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ForumController {
    
    @GetMapping("/forum")
    public String home() {
        return "forum";  // This should be the name of your Thymeleaf template without the .html extension
    }
}
