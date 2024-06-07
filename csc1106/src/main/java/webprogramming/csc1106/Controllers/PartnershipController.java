package webprogramming.csc1106.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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
}
