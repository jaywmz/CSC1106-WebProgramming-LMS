package webprogramming.csc1106.Config;

import com.braintreegateway.BraintreeGateway;
import com.braintreegateway.Environment;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


@Configuration
public class BraintreeConfig {

    private final BraintreeProperties braintreeProperties;

    public BraintreeConfig(BraintreeProperties braintreeProperties) {
        this.braintreeProperties = braintreeProperties;
    }

    @Bean
    public BraintreeGateway braintreeGateway() {
        return new BraintreeGateway(
            braintreeProperties.getEnvironment().equalsIgnoreCase("production") ? Environment.PRODUCTION : Environment.SANDBOX,
            braintreeProperties.getMerchantId(),
            braintreeProperties.getPublicKey(),
            braintreeProperties.getPrivateKey()
        );
    }

}

@Component
@ConfigurationProperties(prefix = "braintree")
class BraintreeProperties {

    private String merchantId;
    private String publicKey;
    private String privateKey;
    private String environment;

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