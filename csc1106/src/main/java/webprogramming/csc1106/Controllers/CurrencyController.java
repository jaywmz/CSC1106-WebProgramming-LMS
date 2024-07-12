package webprogramming.csc1106.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import webprogramming.csc1106.Config.PayPalConfig;

import java.util.List;

@RestController
public class CurrencyController {

    private final PayPalConfig payPalConfig;

    @Autowired
    public CurrencyController(PayPalConfig payPalConfig) {
        this.payPalConfig = payPalConfig;
    }

    @GetMapping("/currencies")
    public List<String> getSupportedCurrencies() {
        return payPalConfig.supportedCurrencies();
    }
}
