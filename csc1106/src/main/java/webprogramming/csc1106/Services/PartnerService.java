package webprogramming.csc1106.Services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import webprogramming.csc1106.Entities.Partner;
import webprogramming.csc1106.Repositories.PartnerRepository;

@Service
public class PartnerService {

    private static final Logger logger = LoggerFactory.getLogger(PartnerService.class);

    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private EmailService emailService;

    public Partner savePartner(Partner partner) {
        return partnerRepository.save(partner);
    }

    public List<Partner> getAllPartners() {
        return partnerRepository.findAll();
    }

    public boolean approvePartner(Integer partnerId) {
        logger.info("Attempting to approve partner with ID: {}", partnerId);
        Partner partner = partnerRepository.findById(partnerId).orElse(null);
        if (partner != null && "Pending".equals(partner.getPartnerStatus())) {
            partner.setPartnerStatus("Approved");
            partnerRepository.save(partner);
            emailService.sendApprovalEmail(partner.getPartnerEmail(), partner.getCompanyName());
            logger.info("Partner with ID {} approved successfully", partnerId);
            return true;
        }
        logger.warn("Failed to approve partner with ID {}. Either partner not found or status is not Pending.", partnerId);
        return false;
    }
}
