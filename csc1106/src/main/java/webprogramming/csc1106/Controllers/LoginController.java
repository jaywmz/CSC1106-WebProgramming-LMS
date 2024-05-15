package webprogramming.csc1106.Controllers;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    private final JdbcTemplate jdbcTemplate;

    public LoginController(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @GetMapping("/login")
    public String loginForm() {
        logger.debug("GET request received for login form");
        return "pages-login"; // Return the login form
    }

    @PostMapping("/login")
    public ModelAndView login(@RequestParam String username, @RequestParam String password) {
        logger.debug("POST request received for login");
        logger.debug("Username: {}", username);
        logger.debug("Password: {}", password);

        ModelAndView modelAndView = new ModelAndView();

        // Query the database for the user with the given username and password
        String sql = "SELECT COUNT(*) FROM Users WHERE userName = ? AND userPassword = ?";
        
        try {
            int count = jdbcTemplate.queryForObject(sql, Integer.class, username, password);

            if (count == 0) {
                // No user found, redirect to login page with an error message
                modelAndView.setViewName("redirect:/login?error=true");
            } else {
                // User found, redirect to dashboard or success page
                modelAndView.setViewName("redirect:/dashboard");
            }
        } catch (EmptyResultDataAccessException e) {
            // No user found, redirect to login page with an error message
            modelAndView.setViewName("redirect:/login?error=true");
        }

        return modelAndView;
    }
}
