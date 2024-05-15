package webprogramming.csc1106.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import webprogramming.csc1106.Entities.User;

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
        System.out.println("Username: " + user.getUserName());
        System.out.println("Password: " + user.getUserPassword());
        return "redirect:/dashboard";
    }
}