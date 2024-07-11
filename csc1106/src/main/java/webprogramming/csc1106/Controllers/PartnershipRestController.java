package webprogramming.csc1106.Controllers;

import java.sql.Timestamp;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import webprogramming.csc1106.Entities.Partner;
import webprogramming.csc1106.Entities.User;
import webprogramming.csc1106.Repositories.PartnerRepository;
import webprogramming.csc1106.Repositories.UserRepository;


@RestController
@RequestMapping("/partnership")
public class PartnershipRestController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PartnerRepository partnerRepository;

    
    //api get to check if partner validity expired 
    @GetMapping("/checkExpired")
    @ResponseBody
    public ResponseEntity<String> checkExpiredSubscription(@RequestParam("userId") int userId) {
    
        Optional<User> optionalUser = userRepository.findById(userId);
    
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
    
        User user = optionalUser.get();
        Partner partner = partnerRepository.findByUser(user);
    
        if (partner == null || partner.getValidityEnd().after(new Timestamp(System.currentTimeMillis()))) {
            return ResponseEntity.ok("false"); // Partnership is not expired
        }
    
        return ResponseEntity.ok("true"); // Partnership is expired
    }
    
    
}
