package webprogramming.csc1106.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import webprogramming.csc1106.Entities.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUserNameAndUserPassword(String userName, String userPassword);
}