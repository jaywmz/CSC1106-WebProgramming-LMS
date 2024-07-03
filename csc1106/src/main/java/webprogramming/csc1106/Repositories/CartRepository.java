package webprogramming.csc1106.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import webprogramming.csc1106.Entities.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {
}
