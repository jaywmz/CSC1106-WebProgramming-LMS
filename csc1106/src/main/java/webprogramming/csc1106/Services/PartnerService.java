package webprogramming.csc1106.Services;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import webprogramming.csc1106.Entities.CourseCategory;
import webprogramming.csc1106.Entities.Partner;
import webprogramming.csc1106.Entities.PartnerPublish;
import webprogramming.csc1106.Entities.Roles;
import webprogramming.csc1106.Entities.User;
import webprogramming.csc1106.Repositories.CourseCategoryRepository;
import webprogramming.csc1106.Repositories.PartnerPublishRepository;
import webprogramming.csc1106.Repositories.PartnerRepository;
import webprogramming.csc1106.Repositories.RoleRepository;
import webprogramming.csc1106.Repositories.UserRepository;

@Service
public class PartnerService {

    private static final Logger logger = LoggerFactory.getLogger(PartnerService.class);

    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EmailService emailService;


    @Autowired
    private PartnerPublishRepository partnerPublishRepository;

    private static final Random random = new Random();

    public Partner savePartner(Partner partner) {
        return partnerRepository.save(partner);
    }

    public List<Partner> getAllPartners() {
        return partnerRepository.findAll();
    }

    public List<PartnerPublish> getPartnerPublishByPartnerId(int partnerId) {
        return partnerPublishRepository.findByPartnerPartnerId(partnerId);
    }

    @Autowired
    private CourseCategoryRepository courseCategoryRepository;

    public List<CourseCategory> getCourseCategoriesByCourseIds(List<Long> courseIds) {
        return courseCategoryRepository.findByCourseIdIn(courseIds);
    }

    public boolean approvePartner(Integer partnerId) {
        logger.info("Attempting to approve partner with ID: {}", partnerId);
        Partner partner = partnerRepository.findById(partnerId).orElse(null);
        if (partner != null && "Pending".equals(partner.getPartnerStatus())) {
            // Generate username and password
            String username = generateUsername(partner.getCompanyName());
            String password = generateRandomPassword();

            // Set User attributes
            User user = new User();
            user.setRole(roleRepository.findById(4).orElseThrow(() -> new RuntimeException("Role not found"))); // Set role to 4
            user.setUserName(username);
            user.setUserPassword(password); // Ideally, password should be hashed before saving
            user.setUserEmail(partner.getPartnerEmail());
            user.setJoinedDate(new Date(System.currentTimeMillis()));
            user.setJoinedTime(new Time(System.currentTimeMillis()));
            user.setUserBalance(new BigDecimal(1000));
            user.setLoginCookie(generateRandomCookie(200));
            userRepository.save(user);

            // Associate User with Partner
            partner.setUser(user);
            partner.setPartnerStatus("Approved");
            partnerRepository.save(partner);

            // Send approval email with credentials
            emailService.sendApprovalEmail(partner.getPartnerEmail(), partner.getCompanyName(), username, password);
            logger.info("Partner with ID {} approved successfully", partnerId);
            return true;
        }
        logger.warn("Failed to approve partner with ID {}. Either partner not found or status is not Pending.", partnerId);
        return false;
    }

    public boolean rejectPartner(Integer partnerId) {
        Partner partner = partnerRepository.findById(partnerId).orElse(null);
        if (partner != null && "Pending".equals(partner.getPartnerStatus())) {
            partner.setPartnerStatus("Rejected");
            partnerRepository.save(partner);
            emailService.sendRejectionEmail(partner.getPartnerEmail(), partner.getCompanyName());
            return true;
        }
        return false;
    }

    private String generateUsername(String companyName) {
        return companyName.replaceAll("\\s+", "") + random.nextInt(10000);
    }

    private String generateRandomPassword() {
        // Generate a random password with a mix of characters
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 12; i++) {
            password.append(characters.charAt(random.nextInt(characters.length())));
        }
        return password.toString();
    }

    private String generateRandomCookie(int length) {
        String cookieCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append(cookieCharacters.charAt(random.nextInt(cookieCharacters.length())));
        }
        return builder.toString();
    }
}
