package webprogramming.csc1106.Controllers;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
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

    
@GetMapping("/checkExpired")
    @ResponseBody
    public ResponseEntity<Map<String, Boolean>> checkExpiredSubscription(@RequestParam("userId") int userId) {

        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = optionalUser.get();
        Partner partner = partnerRepository.findByUser(user);

        if (partner == null) {
            return ResponseEntity.ok(Map.of("isExpired", true, "willExpireSoon", false));
        }

        Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        Timestamp validityEnd = partner.getValidityEnd();
        boolean isExpired = validityEnd.before(currentTimestamp);
        boolean willExpireSoon = validityEnd.toInstant().isBefore(Instant.now().plus(30, ChronoUnit.DAYS));

        return ResponseEntity.ok(Map.of("isExpired", isExpired, "willExpireSoon", willExpireSoon));
    }
    
    
}
