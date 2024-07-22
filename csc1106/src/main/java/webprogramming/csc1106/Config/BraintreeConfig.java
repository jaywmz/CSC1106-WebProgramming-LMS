package webprogramming.csc1106.Config;

// Import necessary packages and classes
import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration // Indicate that this class contains one or more bean methods annotated with @Bean
public class BraintreeConfig {

    private final BraintreeProperties braintreeProperties;

    // Constructor to inject BraintreeProperties
    public BraintreeConfig(BraintreeProperties braintreeProperties) {
        this.braintreeProperties = braintreeProperties;
    }

    // Define a BraintreeGateway bean
    @Bean
    public BraintreeGateway braintreeGateway() {
        // Create a new BraintreeGateway instance using properties from BraintreeProperties
        return new BraintreeGateway(
            braintreeProperties.getEnvironment().equalsIgnoreCase("production") ? Environment.PRODUCTION : Environment.SANDBOX,
            braintreeProperties.getMerchantId(),
            braintreeProperties.getPublicKey(),
            braintreeProperties.getPrivateKey()
        );
    }
}

@Component // Indicate that this class is a Spring component
@ConfigurationProperties(prefix = "braintree") // Bind properties with the prefix "braintree" to this class
class BraintreeProperties {

    private String merchantId;
    private String publicKey;
    private String privateKey;
    private String environment;

    // Getters and Setters
    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }
}
