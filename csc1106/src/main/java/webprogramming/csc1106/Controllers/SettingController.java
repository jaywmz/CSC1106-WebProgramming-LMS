package webprogramming.csc1106.Controllers;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import webprogramming.csc1106.Entities.User;
import webprogramming.csc1106.Repositories.UserRepository;
import webprogramming.csc1106.Securities.Encoding;

@Controller
public class SettingController {

    private final UserRepository userRepository;

    public SettingController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/settings/{email}")
    public ModelAndView userProfile(@PathVariable String email) {
        ModelAndView modelAndView = new ModelAndView("settings");

        // Decode the email address
        String decodedEmail = Encoding.decode(email);

        // Query the database for the user with the given email
        User user = userRepository.findByUserEmail(decodedEmail);

        if (user != null) {
            // Pass the user object to the view
            modelAndView.addObject("user", user);
            modelAndView.addObject("email", email);
        } else {
            // Handle user not found
        }

        return modelAndView;
    }

    @PostMapping("/settings/update")
    public String updateUsername(@RequestParam String email, @RequestParam String username) {
        // Decode the email address
        String decodedEmail = Encoding.decode(email);

        // Query the database for the user with the given email
        User user = userRepository.findByUserEmail(decodedEmail);

        if (user != null) {
            // Update the username
            user.setUserName(username);
            userRepository.save(user);
        } else {
            // Handle user not found
        }

        // Redirect back to the settings page
        return "redirect:/settings/" + email;
    }
    
    @PostMapping("/settings/changePassword")
    public String changePassword(@RequestParam String email, @RequestParam String oldPassword, @RequestParam String newPassword, @RequestParam String confirmPassword) {
        // Decode the email address
        String decodedEmail = Encoding.decode(email);

        // Query the database for the user with the given email
        User user = userRepository.findByUserEmail(decodedEmail);

        if (user != null) {
            // Verify old password
            if (!user.getUserPassword().equals(oldPassword)) {
                // Handle incorrect old password
                return "redirect:/settings/" + email + "?error=incorrectPassword";
            }
            
            // Verify new password and confirm password match
            if (!newPassword.equals(confirmPassword)) {
                // Handle password mismatch
                return "redirect:/settings/" + email + "?error=passwordMismatch";
            }
            
            // Update the password
            user.setUserPassword(newPassword);
            userRepository.save(user);
        } else {
            // Handle user not found
            return "redirect:/settings/" + email + "?error=userNotFound";
        }

        // Redirect back to the settings page with success message
        return "redirect:/settings/" + email + "?success=passwordChanged";
    }
}
