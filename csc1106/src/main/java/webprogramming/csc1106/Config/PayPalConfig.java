package webprogramming.csc1106.Config;

import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class PayPalConfig {

    private final PayPalProperties payPalProperties;

    public PayPalConfig(PayPalProperties payPalProperties) {
        this.payPalProperties = payPalProperties;
    }

    @Bean
    public Map<String, String> paypalSdkConfig() {
        Map<String, String> configMap = new HashMap<>();
        configMap.put("mode", payPalProperties.getMode());
        return configMap;
    }

    @Bean
    public OAuthTokenCredential oAuthTokenCredential() {
        return new OAuthTokenCredential(payPalProperties.getClient().getId(), payPalProperties.getClient().getSecret(), paypalSdkConfig());
    }

    @Bean
    public APIContext apiContext() throws PayPalRESTException {
        APIContext context = new APIContext(payPalProperties.getClient().getId(), payPalProperties.getClient().getSecret(), payPalProperties.getMode());
        context.setConfigurationMap(paypalSdkConfig());
        return context;
    }
}

@Component
@ConfigurationProperties(prefix = "paypal")
class PayPalProperties {

    private Client client = new Client();
    private String mode;

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public static class Client {
        private String id;
        private String secret;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }
    }
}
