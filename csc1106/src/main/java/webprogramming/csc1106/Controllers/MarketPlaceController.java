package webprogramming.csc1106.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
public class MarketPlaceController {

    @GetMapping("/market")
    public String showMarketPlacePage(Model model) {
        return "market"; 
    }
    
    @GetMapping("/checkout")
    public String redirectToCheckout() {
        return "checkout";  
        }
        
    @GetMapping("/IT_Software")
    public String redirectToITandSoftware() {
        return "IT_Software"; // This will return the software template    
    }
    @GetMapping("/business")
    public String redirectToBusiness() {
        return "business"; // This will return the software template
    }
    
    
}
