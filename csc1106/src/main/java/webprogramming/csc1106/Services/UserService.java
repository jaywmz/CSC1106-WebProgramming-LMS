package webprogramming.csc1106.Services;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import webprogramming.csc1106.Entities.User;
import webprogramming.csc1106.Repositories.RoleRepository;
import webprogramming.csc1106.Repositories.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void registerStaff(String username, String password, String email) {
        User user = new User();
        user.setUserName(username);
        user.setUserPassword(password);
        user.setUserEmail(email);
        user.setRole(roleRepository.findById(1).orElseThrow(() -> new RuntimeException("Role not found"))); // Role ID 1 for Staff
        user.setJoinedDate(new java.sql.Date(System.currentTimeMillis()));
        user.setJoinedTime(new java.sql.Time(System.currentTimeMillis()));
        user.setUserBalance(BigDecimal.ZERO); // Or any initial balance
        user.setLoginCookie(generateRandomCookie(200));
        userRepository.save(user);
    }

    private String generateRandomCookie(int length) {
        String cookieCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append(cookieCharacters.charAt(new Random().nextInt(cookieCharacters.length())));
        }
        return builder.toString();
    }

    public Optional<User> findById(int userId) {
        return userRepository.findById(userId);
    }

    public BigDecimal getUserBalance(int userId) {
        return userRepository.findById(userId).map(User::getUserBalance).orElse(BigDecimal.ZERO);
    }

    // Other service methods...
}
