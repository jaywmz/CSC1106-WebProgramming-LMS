package webprogramming.csc1106.Controllers;

import webprogramming.csc1106.Entities.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "pages-login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute User user) {
        // Handle login
        // For now, just print the username and password
        System.out.println("Username: " + user.getUsername());
        System.out.println("Password: " + user.getPassword());
        return "redirect:/dashboard";
    }
}