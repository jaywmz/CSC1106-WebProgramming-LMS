package webprogramming.csc1106.Entities;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.util.List;
import java.util.ArrayList;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userID;

    @Column(name = "role_id", nullable = false)
    private Integer roleID;

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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transactions> transactions = new ArrayList<>();

    // Default constructor
    public User() {}

    // Parameterized constructor
    public User(Integer userID, Integer roleID, String userName, String userPassword, String userEmail, Date joinedDate, Time joinedTime, BigDecimal userBalance) {
        this.userID = userID;
        this.roleID = roleID;
        this.userName = userName;
        this.userPassword = userPassword;
        this.userEmail = userEmail;
        this.joinedDate = joinedDate;
        this.joinedTime = joinedTime;
        this.userBalance = userBalance;
    }

    // Getters and setters
    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public Integer getRoleID() {
        return roleID;
    }

    public void setRoleID(Integer roleID) {
        this.roleID = roleID;
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
}
