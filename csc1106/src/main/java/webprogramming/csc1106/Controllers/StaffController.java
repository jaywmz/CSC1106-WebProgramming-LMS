package webprogramming.csc1106.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import webprogramming.csc1106.Entities.User;
import webprogramming.csc1106.Services.UserService;

@Controller
public class StaffController {

    @Autowired
    private UserService userService;

    @GetMapping("/staff/register")
    public String showStaffRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "Admin/registerStaff";
    }

    @PostMapping("/staff/register")
    public String registerStaff(@RequestParam String username,
                                @RequestParam String password,
                                @RequestParam String email) {
        userService.registerStaff(username, password, email);
      //  return "redirect:/staff/register?success";
        return "redirect:/login";
    }
}
