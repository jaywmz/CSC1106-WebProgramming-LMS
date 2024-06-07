package webprogramming.csc1106.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import webprogramming.csc1106.Entities.Partner;

public interface PartnerRepository extends JpaRepository<Partner, Integer> {
}
