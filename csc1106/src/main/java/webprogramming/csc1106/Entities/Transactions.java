package webprogramming.csc1106.Entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "transactions")
public class Transactions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transactions_id")
    private int transactionsID; // primary key 

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // foreign key reference to User


    @Column(name = "payment_status", nullable = false)
    private String paymentStatus;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Column(name = "transaction_id", nullable = false)
    private String transactionId;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @Column(name = "transaction_timestamp", nullable = false)
    private Timestamp transactionTimestamp;

    // Constructors
    public Transactions() {}

    public Transactions(int transactionsID, User user, String paymentStatus, BigDecimal amount, String transactionId, String paymentMethod, Timestamp transactionTimestamp) {
        this.transactionsID = transactionsID;
        this.user = user;
        this.paymentStatus = paymentStatus;
        this.amount = amount;
        this.transactionId = transactionId;
        this.paymentMethod = paymentMethod;
        this.transactionTimestamp = transactionTimestamp;
    }

    // Getters and setters
    public int getTransactionsID() {
        return transactionsID;
    }

    public void setTransactionsID(int transactionsID) {
        this.transactionsID = transactionsID;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Timestamp getTransactionTimestamp() {
        return transactionTimestamp;
    }

    public void setTransactionTimestamp(Timestamp transactionTimestamp) {
        this.transactionTimestamp = transactionTimestamp;
    }
}
