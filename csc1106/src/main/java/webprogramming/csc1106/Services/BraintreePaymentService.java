package webprogramming.csc1106.Services;

// Import necessary packages and classes
import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Result;
import com.braintreegateway.TransactionRequest;
import com.braintreegateway.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service // Indicate that this class is a Spring service component
public class BraintreePaymentService {

    @Autowired
    private BraintreeGateway braintreeGateway; // Inject the BraintreeGateway

    // Method to create a transaction
    public Result<Transaction> createTransaction(String amount, String paymentMethodNonce) {
        // Create a TransactionRequest with the specified amount and payment method nonce
        TransactionRequest request = new TransactionRequest()
                .amount(new BigDecimal(amount)) // Set the transaction amount
                .paymentMethodNonce(paymentMethodNonce) // Set the payment method nonce
                .options()
                    .submitForSettlement(true) // Submit the transaction for settlement
                    .done();

        // Perform the transaction sale and return the result
        return braintreeGateway.transaction().sale(request);
    }

    // Method to generate a client token
    public String generateClientToken() {
        // Generate and return a client token using the BraintreeGateway
        return braintreeGateway.clientToken().generate();
    }
}
