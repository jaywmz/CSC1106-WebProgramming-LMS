package webprogramming.csc1106.Services;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Result;
import com.braintreegateway.TransactionRequest;
import com.braintreegateway.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BraintreePaymentService {

    @Autowired
    private BraintreeGateway braintreeGateway;

    public Result<Transaction> createTransaction(String amount, String paymentMethodNonce) {
        TransactionRequest request = new TransactionRequest()
                .amount(new BigDecimal(amount))
                .paymentMethodNonce(paymentMethodNonce)
                .options()
                    .submitForSettlement(true)
                    .done();

        return braintreeGateway.transaction().sale(request);
    }

    public String generateClientToken() {
        return braintreeGateway.clientToken().generate();
    }
}
