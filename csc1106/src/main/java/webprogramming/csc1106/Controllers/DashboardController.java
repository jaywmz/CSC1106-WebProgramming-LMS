package webprogramming.csc1106.Controllers;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;
import java.util.List;

import webprogramming.csc1106.Entities.User;
import webprogramming.csc1106.Repositories.UserRepository;
import webprogramming.csc1106.Securities.Encoding;

import webprogramming.csc1106.Entities.DashboardSidebarItems;
import webprogramming.csc1106.Repositories.DashboardSidebarItemsRepository;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
public class DashboardController {
    
    private final UserRepository userRepository;
    private final DashboardSidebarItemsRepository dashboardSidebarItemsRepository;
    
    public DashboardController(
        UserRepository userRepository, 
        DashboardSidebarItemsRepository dashboardSidebarItemsRepository
    ) {
        this.userRepository = userRepository;
        this.dashboardSidebarItemsRepository = dashboardSidebarItemsRepository;
    }

    @GetMapping("/board/{email}")
     public ModelAndView userProfile(@PathVariable String email) {
        ModelAndView modelAndView = new ModelAndView("pages-dashboard");
        // Decode the email address
        String decodedEmail = Encoding.decode(email);
        User user = userRepository.findByUserEmail(decodedEmail);

        if (user != null) {
            // Pass the user object to the view

            modelAndView.addObject("user", user);
            modelAndView.addObject("encoded", email);
        } else {
            // Handle user not found
        }

        return modelAndView;
    }
    
}
