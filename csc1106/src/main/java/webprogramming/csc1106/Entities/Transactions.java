package webprogramming.csc1106.Entities;

// Import necessary packages and classes
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity // Indicate that this class is an entity to be mapped to a database table
@Table(name = "transactions") // Specify the table name in the database
public class Transactions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Indicate that the ID should be generated automatically
    @Column(name = "transactions_id") // Map the field to the "transactions_id" column in the table
    private int transactionsID; // Primary key 

    @ManyToOne // Establish a many-to-one relationship with User
    @JoinColumn(name = "user_id", nullable = false) // Specify the join column for the relationship
    private User user; // Foreign key reference to User

    @Column(name = "payment_status", nullable = false) // Map the field to the "payment_status" column and make it not nullable
    private String paymentStatus;

    @Column(name = "amount", nullable = false) // Map the field to the "amount" column and make it not nullable
    private BigDecimal amount;

    @Column(name = "transaction_id", nullable = false) // Map the field to the "transaction_id" column and make it not nullable
    private String transactionId;

    @Column(name = "payment_method", nullable = false) // Map the field to the "payment_method" column and make it not nullable
    private String paymentMethod;

    @Column(name = "transaction_timestamp", nullable = false) // Map the field to the "transaction_timestamp" column and make it not nullable
    private Timestamp transactionTimestamp;

    // Default constructor
    public Transactions() {}

    // Parameterized constructor
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
