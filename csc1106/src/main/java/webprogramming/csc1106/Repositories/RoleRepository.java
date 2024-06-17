package webprogramming.csc1106.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import webprogramming.csc1106.Entities.Roles;

public interface RoleRepository extends JpaRepository<Roles, Integer> {
}
