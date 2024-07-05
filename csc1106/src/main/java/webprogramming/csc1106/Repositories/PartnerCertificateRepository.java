package webprogramming.csc1106.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import webprogramming.csc1106.Entities.PartnerCertificate;
import webprogramming.csc1106.Entities.PartnerPublish;

import java.util.List;


public interface PartnerCertificateRepository extends JpaRepository<PartnerCertificate, Integer> {
    PartnerCertificate findByPartnerPublish(PartnerPublish partnerPublish);
}
