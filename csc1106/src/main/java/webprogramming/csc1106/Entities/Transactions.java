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
    private int transactionsID; // primary key 

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private UploadCourse course; // foreign key reference to UploadCourse

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // foreign key reference to User

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

    public Transactions(int transactionsID, UploadCourse course, User user, Date transactionDate, Time transactionTime, String paymentStatus, BigDecimal amount) {
        this.transactionsID = transactionsID;
        this.course = course;
        this.user = user;
        this.transactionDate = transactionDate;
        this.transactionTime = transactionTime;
        this.paymentStatus = paymentStatus;
        this.amount = amount;
    }

    // Getters and setters
    public int getTransactionsID() {
        return transactionsID;
    }

    public void setTransactionsID(int transactionsID) {
        this.transactionsID = transactionsID;
    }

    public UploadCourse getCourse() {
        return course;
    }

    public void setCourse(UploadCourse course) {
        this.course = course;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
