package webprogramming.csc1106.Controllers;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import webprogramming.csc1106.Entities.User;
import webprogramming.csc1106.Repositories.UserRepository;
import webprogramming.csc1106.Securities.Encoding;
import webprogramming.csc1106.Services.PayPalService;
import webprogramming.csc1106.Services.UserService;

@Controller
public class UserController {

    private final UserRepository userRepository;
    private final PayPalService payPalService;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public UserController(UserRepository userRepository, PayPalService payPalService, UserService userService) {
        this.userRepository = userRepository;
        this.payPalService = payPalService;
        this.userService = userService;
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
    public ModelAndView login(@RequestParam String email, @RequestParam String password, HttpServletResponse response, HttpSession session) {
        ModelAndView modelAndView = new ModelAndView();

        // Query the database for the user with the given email and password
        User user = userRepository.findByUserEmailAndUserPassword(email, password);

        if (user != null) {
            // Encode email address
            String encodedEmail = Encoding.encode(email);

            // Set userId in session
            session.setAttribute("userId", user.getUserID());

            // Set userId cookie
            Cookie cookie = new Cookie("userId", String.valueOf(user.getUserID()));
            cookie.setPath("/");
            response.addCookie(cookie);

            // Redirect to profile page with email as a parameter
            modelAndView.setViewName("redirect:/board/" + encodedEmail);
        } else {
            // No user found, redirect to login page with an error message
            modelAndView.setViewName("redirect:/login?error=true");
        }

        return modelAndView;
    }

    @PostMapping("/logmein")
    public ResponseEntity<User> logMeIn(@RequestBody Map<String, String> loginData, HttpServletResponse response, HttpSession session) {
        try {
            User user = userRepository.findByUserEmailAndUserPassword(loginData.get("email"), loginData.get("password"));

            if (user == null) {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }

            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            user.setLastLogin(timestamp);

            String cookie = user.generateRandomCookie(200);
            user.setLoginCookie(cookie);

            saveUser(user);

            // Set userId in session
            session.setAttribute("userId", user.getUserID());

            // Set userId cookie
            Cookie userIdCookie = new Cookie("userId", String.valueOf(user.getUserID()));
            userIdCookie.setPath("/");
            response.addCookie(userIdCookie);

            return new ResponseEntity<>(user, HttpStatus.OK);

        } catch (Exception e) {
            logger.error(e.toString());
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/checkmycookie")
    public ResponseEntity<String> checkMyCookie(@RequestBody Map<String, String> cookieData) {
        try {
            String cookie = cookieData.get("cookie");
            User user = userRepository.findByLoginCookie(cookie);
            if (user == null) {
                return new ResponseEntity<>("Invalid Cookie", HttpStatus.BAD_REQUEST);
            }
            String userName = user.getUserName();
            return new ResponseEntity<>(userName, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.toString());
            return new ResponseEntity<>(e.toString(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/checkmycookiepromax")
    public ResponseEntity<User> checkMyCookieProMax(@RequestBody Map<String, String> cookieData) {
        try {
            String cookie = cookieData.get("cookie");
            User user = userRepository.findByLoginCookie(cookie);
            if (user == null) {
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
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

    @GetMapping("/paypal/pay")
    public RedirectView pay(@RequestParam("total") Double total, @RequestParam("userId") int userId) {
        String cancelUrl = "http://localhost:8080/paypal/cancel";
        String successUrl = "http://localhost:8080/paypal/success?userId=" + userId;
        try {
            Payment payment = payPalService.createPayment(total, "USD", "paypal", "sale", "Top up eCredit", cancelUrl, successUrl);
            for (com.paypal.api.payments.Links links : payment.getLinks()) {
                if (links.getRel().equals("approval_url")) {
                    return new RedirectView(links.getHref());
                }
            }
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }
        return new RedirectView(cancelUrl);
    }

    @GetMapping("/paypal/success")
    public String success(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId, @RequestParam("userId") int userId, Model model) {
        try {
            logger.info("Executing payment with paymentId: " + paymentId + " and payerId: " + payerId);
            Payment payment = payPalService.executePayment(paymentId, payerId);
            logger.info("Payment executed with state: " + payment.getState());
            
            if (payment.getState().equals("approved")) {
                // Update user's eCredit balance in the database
                Optional<User> optionalUser = userRepository.findById(userId);
                if (optionalUser.isPresent()) {
                    User user = optionalUser.get();
                    BigDecimal amount = new BigDecimal(payment.getTransactions().get(0).getAmount().getTotal());
                    user.setUserBalance(user.getUserBalance().add(amount));
                    userRepository.save(user);
                    logger.info("User balance updated for userId: " + userId + " with amount: " + amount);
    
                    // Add attributes to model
                    model.addAttribute("amount", amount);
                    model.addAttribute("transactionId", paymentId);
                    return "Marketplace/confirmation"; // Return confirmation view
                } else {
                    logger.error("User not found for userId: " + userId);
                    model.addAttribute("message", "User not found.");
                    return "Marketplace/error"; // Return error view
                }
            } else {
                logger.error("Payment not approved. State: " + payment.getState());
                model.addAttribute("message", "Payment not approved.");
                return "Marketplace/error"; // Return error view
            }
        } catch (PayPalRESTException e) {
            logger.error("PayPalRESTException: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("message", "PayPalRESTException: " + e.getMessage());
            return "Marketplace/error"; // Return error view
        } catch (Exception e) {
            logger.error("Exception: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("message", "Exception: " + e.getMessage());
            return "Marketplace/error"; // Return error view
        }
    }
    
    @GetMapping("/paypal/cancel")
    public String cancel() {
        return "Payment canceled!";
    }
    
    @GetMapping("/user/{userId}/balance")
    public ResponseEntity<Map<String, Object>> getUserBalance(@PathVariable int userId) {
        Optional<User> optionalUser = userService.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Map<String, Object> response = new HashMap<>();
            response.put("balance", user.getUserBalance());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    
    @GetMapping("/user/{userId}/role")
    public ResponseEntity<Map<String, String>> getUserRole(@PathVariable int userId) {
        Optional<User> userOptional = userService.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Map<String, String> response = new HashMap<>();
            response.put("role", user.getRole().getRoleName());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    
    private void saveUser(User user) {
        userRepository.save(user);
        logger.debug("User data saved to the database");
    }
}
