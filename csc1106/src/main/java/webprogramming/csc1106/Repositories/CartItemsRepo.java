package webprogramming.csc1106.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;


import webprogramming.csc1106.Entities.CartItemEntity;

public interface CartItemsRepo extends JpaRepository<CartItemEntity, String>{

        @Query("SELECT c FROM CartItemEntity c WHERE c.userId = :userId")
        List<CartItemEntity> getCartItemsByUserId(@Param("userId") int userId);
        
        @Query("SELECT c FROM CartItemEntity c")
        List<CartItemEntity> getAllCartItems();

        //Delete item with id
        @Transactional
        @Modifying
        @Query("DELETE FROM CartItemEntity c WHERE c.id = :id")
        void deleteCartItemById(@Param("id") String id);
}
