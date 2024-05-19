package webprogramming.csc1106.Entities;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userID;

    @Column(name = "role_id")
    private Integer roleID;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "user_password")
    private String userPassword;

    @Column(name = "user_email")
    private String userEmail;

    @Column(name = "joined_date")
    private Date joineddDate;

    @Column(name = "joined_time")
    private Time joinedTime;

    @Column(name = "user_balance")
    private BigDecimal userBalance;

    // default constructor
    public User() {
    }

    public User(Integer userID, Integer roleID, String userName, String userPassword, String userEmail, Date joineddDate, Time joinedTime, BigDecimal userBalance) {
        this.userID = userID;
        this.roleID = roleID;
        this.userName = userName;
        this.userPassword = userPassword;
        this.userEmail = userEmail;
        this.joineddDate = joineddDate;
        this.joinedTime = joinedTime;
        this.userBalance = userBalance;
    }

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

    public Date getJoineddDate() {
        return joineddDate;
    }

    public void setJoineddDate(Date joineddDate) {
        this.joineddDate = joineddDate;
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
}
