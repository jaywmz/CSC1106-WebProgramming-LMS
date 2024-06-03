package webprogramming.csc1106.Entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;

@Entity
@Table(name = "transactions")
public class Transactions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transactions_id")
    private int TransactionsID; // primary key 

    @Column(name = "course_id", nullable = false)
    private int courseID; // foreign key

    @Column(name = "user_id", nullable = false)
    private int userID; // foreign key

    @Column(name = "transaction_date", nullable = false)
    private Date transactionDate;

    @Column(name = "transaction_time", nullable = false)
    private Time transactionTime;

    @Column(name = "payment_status", nullable = false)
    private String paymentStatus;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    // Constructors
    public Transactions() {}

    public Transactions(int TransactionsID, int courseID, int userID, Date transactionDate, Time transactionTime, String paymentStatus, BigDecimal amount) {
        this.TransactionsID = TransactionsID;
        this.courseID = courseID;
        this.userID = userID;
        this.transactionDate = transactionDate;
        this.transactionTime = transactionTime;
        this.paymentStatus = paymentStatus;
        this.amount = amount;
    }

    // Getters and setters
    public int getTransactionsID() {
        return TransactionsID;
    }

    public void setTransactionsID(int TransactionsID) {
        this.TransactionsID = TransactionsID;
    }

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Time getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(Time transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
