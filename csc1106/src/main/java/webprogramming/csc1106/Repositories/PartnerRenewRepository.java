package webprogramming.csc1106.Repositories;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import webprogramming.csc1106.Entities.Partner;
import webprogramming.csc1106.Entities.PartnerRenew;

@Repository
public interface PartnerRenewRepository extends JpaRepository<PartnerRenew, Integer> {
    List<PartnerRenew> findAllByRenewStatus(String renewStatus);
    PartnerRenew findByPartnerAndRenewStatus(Partner partner, String renewStatus);
}