package webprogramming.csc1106.Controllers;

import java.sql.Date;
import java.sql.Time;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import webprogramming.csc1106.Entities.User;


@Controller
public class RegisterController {

    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);

    private final JdbcTemplate jdbcTemplate;

    public RegisterController(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @GetMapping("/register")
    public String registerForm() {
        logger.debug("GET request received for registration form");
        return "pages-register"; // Return the registration form
    }

    @PostMapping("/register")
    public String registerForm(@ModelAttribute User user) {
        logger.debug("POST request received for registration form submission");
        logger.debug("Received user registration form submission:");
        logger.debug("User ID: {}", user.getUserID());
        logger.debug("Role ID: {}", user.getRoleID());
        logger.debug("Username: {}", user.getUserName());
        logger.debug("User Password: {}", user.getUserPassword());
        logger.debug("User Email: {}", user.getUserEmail());
        logger.debug("Joined Date: {}", user.getJoineddDate());
        logger.debug("Joined Time: {}", user.getJoinedTime());

        // Set joinedDate and joinedTime
        user.setJoineddDate(new Date(System.currentTimeMillis()));
        user.setJoinedTime(new Time(System.currentTimeMillis()));

        // Save user data
        saveUser(user);

        // Redirect to a success page or dashboard after successful registration
        logger.debug("Redirecting to /dashboard after successful registration");
        return "redirect:/dashboard";
    }

    private void saveUser(User user) {
        String sql = "INSERT INTO Users (userID, userName, userPassword,userEmail, roleID) VALUES (?,?, ?,?,?)";
        jdbcTemplate.update(sql, user.getUserID(), user.getUserName(), user.getUserPassword(), user.getUserEmail(),user.getRoleID());
        logger.debug("User data saved to the database");
    }


}
