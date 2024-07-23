package webprogramming.csc1106.Services;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendApprovalEmail(String toEmail, String companyName, String username, String password) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("learnzenith72@outlook.com");
            message.setTo(toEmail);
            message.setSubject("Partnership Approval Notification");
            message.setText("Dear " + companyName + ",\n\n" +
                    "Your partnership application has been approved. Welcome aboard!\n\n" +
                    "Here are your login credentials:\n" +
                    "Username: " + username + "\n" +
                    "Password: " + password + "\n\n" +
                    "Best regards,\n" +
                    "Web Programming Team");

            mailSender.send(message);
        } catch (Exception e) {
            // Log the error
            System.err.println("Failed to send email: " + e.getMessage());
            // Re-throw the exception to ensure the error is handled appropriately
            throw new RuntimeException("Failed to send approval email", e);
        }
    }

    public void sendRejectionEmail(String toEmail, String companyName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("learnzenith72@outlook.com");
            message.setTo(toEmail);
            message.setSubject("Partnership Rejection Notification");
            message.setText("Dear " + companyName + ",\n\n" +
                    "We regret to inform you that your partnership application has been rejected. Thank you for your interest.\n\n" +
                    "Best regards,\n" +
                    "Web Programming Team");

            mailSender.send(message);
        } catch (Exception e) {
            // Log the error
            System.err.println("Failed to send email: " + e.getMessage());
            // Re-throw the exception to ensure the error is handled appropriately
            throw new RuntimeException("Failed to send rejection email", e);
        }
    }


    //send renewal success email for partner subscription
    public void sendRenewalSuccessEmail(String toEmail, String companyName, Timestamp validityEnd)
    {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("learnzenith72@outlook.com");
            message.setTo(toEmail);
            message.setSubject("Subscription Renewal Notification");
            message.setText("Dear " + companyName + ",\n\n" +
                    "Your subscription has been approved and successfully renewed. Thank you for your continued partnership.\n\n" +
                    "Your new validity end date is: " + validityEnd + "\n\n" +
                    "Best regards,\n" +
                    "Web Programming Team");
            mailSender.send(message);
        } catch (Exception e) {
            // Log the error
            System.err.println("Failed to send email: " + e.getMessage());
            // Re-throw the exception to ensure the error is handled appropriately
            throw new RuntimeException("Failed to send renewal success email", e);
        }
    }
    
}
