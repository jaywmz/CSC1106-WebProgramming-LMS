package webprogramming.csc1106.Controllers;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import webprogramming.csc1106.Entities.User;
import webprogramming.csc1106.Repositories.UserRepository;
import webprogramming.csc1106.Securities.Encoding;

@Controller
public class UserController {

    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/login")
    public String loginForm() {
        
        return "User/pages-login"; // Return the login form
        
    }

    @GetMapping("/register")
    public String registerForm() {
        logger.debug("GET request received for registration form");
        return "User/pages-register"; // Return the registration form
    }

    @PostMapping("/login")
    public ModelAndView login(@RequestParam String email, @RequestParam String password) {
        ModelAndView modelAndView = new ModelAndView();

        // Query the database for the user with the given email and password
        User user = userRepository.findByUserEmailAndUserPassword(email, password);

        if (user != null) {
            // Encode email address
            String encodedEmail = Encoding.encode(email);
            // Redirect to profile page with email as a parameter
            modelAndView.setViewName("redirect:/board/" + encodedEmail);
        } else {
            // No user found, redirect to login page with an error message
            modelAndView.setViewName("redirect:/login?error=true");
        }

        return modelAndView;
    }

    @PostMapping("/logmein")
    public ResponseEntity<User> logMeIn(@RequestBody Map<String, String> loginData){
        
        try{
            User user = userRepository.findByUserEmailAndUserPassword(loginData.get("email"), loginData.get("password"));
            
            if(user == null){
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            user.setLastLogin(timestamp);

            String cookie = user.generateRandomCookie(200);
            user.setLoginCookie(cookie);

            saveUser(user);

            return new ResponseEntity<>(user, HttpStatus.OK);

        }catch(Exception e){
            logger.error(e.toString());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    
    }

    @PostMapping("/checkmycookie")
    public ResponseEntity<String> checkMyCookie(@RequestBody Map<String, String> cookieData){
        try{
            String cookie = cookieData.get("cookie");
            User user = userRepository.findByLoginCookie(cookie);
            if(user == null){
                return new ResponseEntity<>("Invalid Cookie", HttpStatus.BAD_REQUEST);
            }
            String userName = user.getUserName();
            return new ResponseEntity<>(userName, HttpStatus.OK);
        }catch(Exception e){
            logger.error(e.toString());
            return new ResponseEntity<>(e.toString(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/checkmycookiepromax")
    public ResponseEntity<User> checkMyCookieProMax(@RequestBody Map<String, String> cookieData){
        try{
            String cookie = cookieData.get("cookie");
            User user = userRepository.findByLoginCookie(cookie);
            if(user == null){
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(user, HttpStatus.OK);
        }catch(Exception e){
            logger.error(e.toString());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/register")
    public String registerForm(@ModelAttribute User user) {
        logger.debug("POST request received for registration form submission");
        logger.debug("Received user registration form submission:");
        logger.debug("User ID: {}", user.getUserID());
        logger.debug("Role ID: {}", user.getRole().getRoleID());
        logger.debug("Username: {}", user.getUserName());
        logger.debug("User Password: {}", user.getUserPassword());
        logger.debug("User Email: {}", user.getUserEmail());
        logger.debug("Joined Date: {}", user.getJoinedDate());
        logger.debug("Joined Time: {}", user.getJoinedTime());

        // Set joinedDate and joinedTime
        user.setJoinedDate(new Date(System.currentTimeMillis()));
        user.setJoinedTime(new Time(System.currentTimeMillis()));
        
        // Set initial balance to 1000
        user.setUserBalance(new BigDecimal(1000));

        // Save user data
        saveUser(user);

        // Redirect to a success page or dashboard after successful registration
        logger.debug("Redirecting to /dashboard after successful registration");
        return "redirect:/login";
    }

    private void saveUser(User user) {
        userRepository.save(user);
        logger.debug("User data saved to the database");
    }

}
