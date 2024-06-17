package webprogramming.csc1106.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import webprogramming.csc1106.Entities.CartItemEntity;

public interface CartItemsRepo extends JpaRepository<CartItemEntity, String>{
        
        @Query("SELECT c FROM CartItemEntity c")
        List<CartItemEntity> getAllCartItems();
}
