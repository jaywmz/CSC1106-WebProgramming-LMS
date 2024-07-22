package webprogramming.csc1106.Repositories;

// Import necessary packages and classes
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import webprogramming.csc1106.Entities.Transactions;

@Repository // Indicate that this interface is a Spring Data JPA repository
public interface TransactionsRepository extends JpaRepository<Transactions, Integer> {
    // This interface inherits all the methods for CRUD operations from JpaRepository
}
