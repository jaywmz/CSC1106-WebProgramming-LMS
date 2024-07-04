package webprogramming.csc1106.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import webprogramming.csc1106.Entities.Partner;
import webprogramming.csc1106.Entities.User;

public interface PartnerRepository extends JpaRepository<Partner, Integer> {
    Partner findByUserUserId(int userId);
    Partner findByUser(User user);
}
