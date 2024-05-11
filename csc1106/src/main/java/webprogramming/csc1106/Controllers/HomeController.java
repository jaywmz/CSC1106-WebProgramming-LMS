package webprogramming.csc1106.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";  // This should be the name of your Thymeleaf template without the .html extension
    }

}