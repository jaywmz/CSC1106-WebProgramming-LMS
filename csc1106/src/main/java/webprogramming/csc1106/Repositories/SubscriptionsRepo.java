package webprogramming.csc1106.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import webprogramming.csc1106.Entities.Subscription;
import webprogramming.csc1106.Models.SubscribeID;

public interface SubscriptionsRepo extends JpaRepository<Subscription, SubscribeID>{
    
}