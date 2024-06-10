package webprogramming.csc1106.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendApprovalEmail(String toEmail, String companyName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("learnzenith7@outlook.com");
            message.setTo(toEmail);
            message.setSubject("Partnership Approval Notification");
            message.setText("Dear " + companyName + ",\n\nYour partnership application has been approved. Welcome aboard!\n\nBest regards,\nWeb Programming Team");

            mailSender.send(message);
        } catch (Exception e) {
            // Log the error
            System.err.println("Failed to send email: " + e.getMessage());
            // Re-throw the exception to ensure the error is handled appropriately
            throw new RuntimeException("Failed to send approval email", e);
        }
    }


}
