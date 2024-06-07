package webprogramming.csc1106.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import webprogramming.csc1106.Entities.Partner;
import webprogramming.csc1106.Repositories.PartnerRepository;

@Service
public class PartnerService {

    @Autowired
    private PartnerRepository partnerRepository;

    public Partner savePartner(Partner partner) {
        return partnerRepository.save(partner);
    }
}
