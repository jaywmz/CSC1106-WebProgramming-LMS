package webprogramming.csc1106.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import webprogramming.csc1106.Entities.Partner;
import webprogramming.csc1106.Services.PartnerService;

@Controller
@RequestMapping("/partnership")
public class PartnershipController {

    @Autowired
    private PartnerService partnerService;

    @GetMapping
    public String showPartnershipPage() {
        return "User/partnership";
    }

    @PostMapping("/submit")
    @ResponseBody
    public String submitPartnershipApplication(
        @RequestParam("companyName") String companyName,
        @RequestParam("partnerEmail") String partnerEmail,
        @RequestParam("businessSector") String businessSector,
        @RequestParam("approvalReason") String approvalReason) {

        Partner partner = new Partner();
        partner.setCompanyName(companyName);
        partner.setPartnerEmail(partnerEmail);
        partner.setApprovalReason(approvalReason);
        partner.setPartnerStatus("Pending");  // Set default status as pending

        partnerService.savePartner(partner);

        return "{\"message\":\"Your application has been submitted successfully!\"}";
    }

    @GetMapping("/list")
    public String listPartners(Model model) {
        List<Partner> partners = partnerService.getAllPartners();
        model.addAttribute("partners", partners);
        return "User/partnershiplisting";
    }

    @PostMapping("/approve/{partnerId}")
    @ResponseBody
    public ResponseEntity<String> approvePartner(@PathVariable Integer partnerId) {
        try {
            boolean success = partnerService.approvePartner(partnerId);
            if (success) {
                return ResponseEntity.ok("{\"success\": true}");
            } else {
                return ResponseEntity.status(500).body("{\"success\": false, \"error\": \"Failed to approve partner.\"}");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"success\": false, \"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @PostMapping("/reject/{partnerId}")
    @ResponseBody
    public ResponseEntity<String> rejectPartner(@PathVariable Integer partnerId) {
        try {
            boolean success = partnerService.rejectPartner(partnerId);
            if (success) {
                return ResponseEntity.ok("{\"success\": true}");
            } else {
                return ResponseEntity.status(500).body("{\"success\": false, \"error\": \"Failed to reject partner.\"}");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"success\": false, \"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
