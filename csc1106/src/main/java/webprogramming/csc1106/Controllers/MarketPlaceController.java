package webprogramming.csc1106.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
public class MarketPlaceController {

    @GetMapping("/Market")
    public String showMarketPlacePage(Model model) {
        return "Market"; 
    }
    @GetMapping("/checkout")
    public String redirectToCheckout() {
        return "checkout";  // when click the cart icon in the market page, it will redirect to the checkout page
    }
}