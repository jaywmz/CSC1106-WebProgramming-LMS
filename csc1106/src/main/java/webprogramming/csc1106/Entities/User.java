package webprogramming.csc1106.Entities;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;


@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userID;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Roles role;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "user_password", nullable = false)
    private String userPassword;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "joined_date", nullable = false)
    private Date joinedDate;

    @Column(name = "joined_time", nullable = false)
    private Time joinedTime;

    @Column(name = "user_balance", nullable = false, precision = 38, scale = 2)
    private BigDecimal userBalance;

    @Column(name = "last_login", nullable = true)
    private Timestamp lastLogin;

    @Column(name = "login_cookie", nullable = true)
    private String loginCookie;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transactions> transactions = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Likes> likes;

    // Default constructor
    public User() {}

    // Parameterized constructor
    public User(Integer userID, Roles role, String userName, String userPassword, String userEmail, Date joinedDate, Time joinedTime, BigDecimal userBalance, Timestamp lastLogin, String loginCookie) {
        this.userID = userID;
        this.role = role;
        this.userName = userName;
        this.userPassword = userPassword;
        this.userEmail = userEmail;
        this.joinedDate = joinedDate;
        this.joinedTime = joinedTime;
        this.userBalance = userBalance;
        this.lastLogin = lastLogin;
        this.loginCookie = loginCookie;
    }

    // Getters and setters
    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public Roles getRole() {
        return role;
    }

    public void setRole(Roles role) {
        this.role = role;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Date getJoinedDate() {
        return joinedDate;
    }

    public void setJoinedDate(Date joinedDate) {
        this.joinedDate = joinedDate;
    }

    public Time getJoinedTime() {
        return joinedTime;
    }

    public void setJoinedTime(Time joinedTime) {
        this.joinedTime = joinedTime;
    }

    public BigDecimal getUserBalance() {
        return userBalance;
    }

    public void setUserBalance(BigDecimal userBalance) {
        this.userBalance = userBalance;
    }

    public List<Transactions> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transactions> transactions) {
        this.transactions = transactions;
    }

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getLoginCookie() {
        return loginCookie;
    }

    public void setLoginCookie(String loginCookie) {
        this.loginCookie = loginCookie;
    }

    // Generate Random String with Numbers
    private static Random random = new Random();

    public String generateRandomCookie(int length){
        String cookieCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + 
                            "abcdefghijklmnopqrstuvwxyz" + 
                            "0123456789" + 
                            "!@#$%^&*()";

        StringBuilder builder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            builder.append(cookieCharacters.charAt(random.nextInt(cookieCharacters.length())));
        }

        return builder.toString();
    }
}
