package webprogramming.csc1106.Services;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.Random;
import java.io.IOException;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

import webprogramming.csc1106.Entities.User;
import webprogramming.csc1106.Repositories.RoleRepository;
import webprogramming.csc1106.Repositories.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AzureBlobService azureBlobService;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, AzureBlobService azureBlobService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.azureBlobService = azureBlobService;
    }

    public User saveUser(User user) {
        if (!isPasswordComplex(user.getUserPassword())) {
            throw new IllegalArgumentException("Password does not meet complexity requirements.");
        }
        user.setUserPassword(hashPassword(user.getUserPassword()));
        return userRepository.save(user);
    }

    public void registerStaff(String username, String password, String email) {
        if (!isPasswordComplex(password)) {
            throw new IllegalArgumentException("Password does not meet complexity requirements.");
        }

        User user = new User();
        user.setUserName(username);
        user.setUserPassword(hashPassword(password)); // Hash the password
        user.setUserEmail(email);
        user.setRole(roleRepository.findById(1).orElseThrow(() -> new RuntimeException("Role not found"))); // Role ID 1 for Staff
        user.setJoinedDate(new java.sql.Date(System.currentTimeMillis()));
        user.setJoinedTime(new java.sql.Time(System.currentTimeMillis()));
        user.setUserBalance(BigDecimal.ZERO); // Or any initial balance
        user.setLoginCookie(generateRandomCookie(200));
        userRepository.save(user);
    }

    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    private boolean isPasswordComplex(String password) {
        if (password.length() < 8) {
            return false;
        }
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        Pattern p = Pattern.compile(regex);
        return p.matcher(password).matches();
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

    public void updateUserBalance(int userId, BigDecimal newBalance) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setUserBalance(newBalance);
            userRepository.save(user);
        });
    }

    public String uploadProfilePicture(MultipartFile file, int userId) {
        // Remove the existing profile picture from Azure Blob Storage
        userRepository.findById(userId).ifPresent(user -> {
            String existingPictureUrl = user.getProfilePicture();
            if (existingPictureUrl != null) {
                azureBlobService.deleteBlob(existingPictureUrl);
            }
        });

        // Convert the MultipartFile to InputStream
        InputStream fileInputStream = null;
        try {
            fileInputStream = file.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String pictureUrl = null;
        try {
            pictureUrl = azureBlobService.uploadToAzureBlob(fileInputStream, "profile-picture-" + userId + ".jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Update the user's profile picture URL in the database
        final String finalPictureUrl = pictureUrl;
        userRepository.findById(userId).ifPresent(user -> {
            user.setProfilePicture(finalPictureUrl);
            userRepository.save(user);
        });

        // Return the URL of the uploaded picture
        return pictureUrl;
    }

    // Other service methods...
}
