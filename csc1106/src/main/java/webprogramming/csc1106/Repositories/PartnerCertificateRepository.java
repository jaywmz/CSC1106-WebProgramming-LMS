package webprogramming.csc1106.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import webprogramming.csc1106.Entities.PartnerCertificate;

public interface PartnerCertificateRepository extends JpaRepository<PartnerCertificate, Integer> {
}