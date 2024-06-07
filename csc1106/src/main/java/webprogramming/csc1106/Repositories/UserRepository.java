package webprogramming.csc1106.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import webprogramming.csc1106.Entities.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUserEmailAndUserPassword(String userEmail, String userPassword); // For Login Purposes
    User findByUserName(String userName); //For getting username
    User findByRoleRoleID(Integer roleID);
    User findByUserEmail(String userEmail); // For getting user by email
    //User findByHashedUserName(String hashedUserName); // New method to find user by hashed username
    
    // Find User by Cookie
    User findByLoginCookie(String loginCookie);
}
