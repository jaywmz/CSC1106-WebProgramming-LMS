package webprogramming.csc1106.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import webprogramming.csc1106.Entities.Subscription;
import webprogramming.csc1106.Entities.User;
import webprogramming.csc1106.Models.SubscribeID;

public interface SubscriptionsRepo extends JpaRepository<Subscription, SubscribeID>{
    List<Subscription> findAllByUser(User user);
}