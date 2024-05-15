package webprogramming.csc1106.Entities;
import java.sql.Date;
import java.sql.Time;


public class User {
    
    private Integer userID; //primary key
    private Integer roleID; //foreign key
    private String userName; //this can be used to store varchar data from sql (varchar)
    private String userPassword; //this can be used to store varchar data from sql (varchar)
    private String userEmail; //this can be used to store varchar data from sql (varchar)
    private Date joineddDate;
    private Time joinedTime;


    public User(Integer userID, Integer roleID, String userName, String userPassword, String userEmail, Date joineddDate, Time joinedTime) {
        this.userID = userID;
        this.roleID = roleID;
        this.userName = userName;
        this.userPassword = userPassword;
        this.userEmail = userEmail;
        this.joineddDate = joineddDate;
        this.joinedTime = joinedTime;
    }
    public int getUserID() {
        return userID;
    }
    public void setUserID(int userID) {
        this.userID = userID;
    }
    public int getRoleID() {
        return roleID;
    }
    public void setRoleID(int roleID) {
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
}
