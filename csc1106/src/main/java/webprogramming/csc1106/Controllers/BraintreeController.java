package webprogramming.csc1106.Controllers;

// Import necessary packages and classes
import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Result;
import com.braintreegateway.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Optional;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import webprogramming.csc1106.Entities.Transactions;
import webprogramming.csc1106.Entities.User;
import webprogramming.csc1106.Repositories.TransactionsRepository;
import webprogramming.csc1106.Repositories.UserRepository;
import webprogramming.csc1106.Services.BraintreePaymentService;

@Controller // Indicate that this class is a Spring MVC controller
@RequestMapping("/braintree") // Map requests to /braintree to this controller
public class BraintreeController {

    private final BraintreeGateway braintreeGateway;
    private final BraintreePaymentService braintreePaymentService;
    private final UserRepository userRepository;
    private final TransactionsRepository transactionsRepository;

    @Autowired
    public BraintreeController(BraintreeGateway braintreeGateway, BraintreePaymentService braintreePaymentService, UserRepository userRepository, TransactionsRepository transactionsRepository) {
        this.braintreeGateway = braintreeGateway;
        this.braintreePaymentService = braintreePaymentService;
        this.userRepository = userRepository;
        this.transactionsRepository = transactionsRepository;
    }

    // Handle GET requests to /client-token URL
    @GetMapping("/client-token")
    public ResponseEntity<String> generateClientToken() {
        try {
            String clientToken = braintreeGateway.clientToken().generate();
            return new ResponseEntity<>(clientToken, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Handle GET requests to /pay URL to initiate payment
    @GetMapping("/pay")
    public String pay(@RequestParam("total") String total, @RequestParam("userId") int userId, Model model) {
        try {
            String clientToken = braintreePaymentService.generateClientToken();
            model.addAttribute("clientToken", clientToken);
            model.addAttribute("total", total);
            model.addAttribute("userId", userId);
            return "Marketplace/braintree-checkout"; // Return the checkout view
        } catch (Exception e) {
            model.addAttribute("message", "Exception: " + e.getMessage());
            return "Marketplace/error"; // Return the error view
        }
    }

    // Handle POST requests to /checkout URL for processing payment
    @PostMapping("/checkout")
    public String checkout(@RequestParam("amount") String amount, @RequestParam("paymentMethodNonce") String paymentMethodNonce, @RequestParam("userId") int userId, Model model) {
        try {
            Result<Transaction> result = braintreePaymentService.createTransaction(amount, paymentMethodNonce);
            if (result.isSuccess()) {
                Transaction transaction = result.getTarget();
                Optional<User> optionalUser = userRepository.findById(userId);
                if (optionalUser.isPresent()) {
                    User user = optionalUser.get();
                    BigDecimal amountDecimal = new BigDecimal(amount);
                    user.setUserBalance(user.getUserBalance().add(amountDecimal));
                    userRepository.save(user);

                    // Create a new Transactions record
                    Transactions transactions = new Transactions();
                    transactions.setAmount(amountDecimal);
                    transactions.setUser(user);
                    transactions.setPaymentStatus(transaction.getStatus().toString());
                    transactions.setTransactionId(transaction.getId());
                    transactions.setPaymentMethod(transaction.getCreditCard().getCardType());
                    Timestamp timestamp = Timestamp.from(Instant.now());
                    transactions.setTransactionTimestamp(timestamp);
                    
                    transactionsRepository.save(transactions);

                    // Format timestamp to 24-hour format in Singapore time zone
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm", Locale.ENGLISH).withZone(ZoneId.of("Asia/Singapore"));
                    String formattedTimestamp = formatter.format(timestamp.toInstant());

                    model.addAttribute("amount", amountDecimal);
                    model.addAttribute("transactionId", transaction.getId());
                    model.addAttribute("cardType", transaction.getCreditCard().getCardType());
                    model.addAttribute("transactionTimestamp", formattedTimestamp);
                    return "Marketplace/confirmation"; // Return the confirmation view
                } else {
                    model.addAttribute("message", "User not found.");
                    return "Marketplace/error"; // Return the error view
                }
            } else {
                model.addAttribute("message", "Transaction failed: " + result.getMessage());
                return "Marketplace/error"; // Return the error view
            }
        } catch (Exception e) {
            model.addAttribute("message", "Exception: " + e.getMessage());
            return "Marketplace/error"; // Return the error view
        }
    }

    // Handle GET requests to /cancel URL
    @GetMapping("/cancel")
    public String cancel() {
        return "Payment canceled!";
    }
}
