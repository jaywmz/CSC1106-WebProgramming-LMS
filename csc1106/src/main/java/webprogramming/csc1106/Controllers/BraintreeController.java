package webprogramming.csc1106.Controllers;

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
import java.util.Optional;

import webprogramming.csc1106.Entities.User;
import webprogramming.csc1106.Repositories.UserRepository;
import webprogramming.csc1106.Services.BraintreePaymentService;

@Controller
@RequestMapping("/braintree")
public class BraintreeController {

    private final BraintreeGateway braintreeGateway;
    private final BraintreePaymentService braintreePaymentService;
    private final UserRepository userRepository;

    @Autowired
    public BraintreeController(BraintreeGateway braintreeGateway, BraintreePaymentService braintreePaymentService, UserRepository userRepository) {
        this.braintreeGateway = braintreeGateway;
        this.braintreePaymentService = braintreePaymentService;
        this.userRepository = userRepository;
    }

    @GetMapping("/client-token")
    public ResponseEntity<String> generateClientToken() {
        try {
            String clientToken = braintreeGateway.clientToken().generate();
            return new ResponseEntity<>(clientToken, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/pay")
    public String pay(@RequestParam("total") String total, @RequestParam("userId") int userId, Model model) {
        try {
            String clientToken = braintreePaymentService.generateClientToken();
            model.addAttribute("clientToken", clientToken);
            model.addAttribute("total", total);
            model.addAttribute("userId", userId);
            return "Marketplace/braintree-checkout";
        } catch (Exception e) {
            model.addAttribute("message", "Exception: " + e.getMessage());
            return "Marketplace/error";
        }
    }

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
                    model.addAttribute("amount", amountDecimal);
                    model.addAttribute("transactionId", transaction.getId());
                    return "Marketplace/confirmation";
                } else {
                    model.addAttribute("message", "User not found.");
                    return "Marketplace/error";
                }
            } else {
                model.addAttribute("message", "Transaction failed: " + result.getMessage());
                return "Marketplace/error";
            }
        } catch (Exception e) {
            model.addAttribute("message", "Exception: " + e.getMessage());
            return "Marketplace/error";
        }
    }

    @GetMapping("/cancel")
    public String cancel() {
        return "Payment canceled!";
    }
}
