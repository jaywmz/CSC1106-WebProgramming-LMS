package webprogramming.csc1106.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import webprogramming.csc1106.Entities.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
