package webprogramming.csc1106.Entities;

import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;  // Primary key for the Cart entity, automatically generated

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();  // List to hold CartItem entities associated with this Cart

    // Default constructor
    public Cart() {}

    // Getter for the 'id' field
    public Long getId() {
        return id;
    }

    // Setter for the 'id' field
    public void setId(Long id) {
        this.id = id;
    }

    // Getter for the 'items' list
    public List<CartItem> getItems() {
        return items;
    }

    // Setter for the 'items' list
    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    // Method to add a CartItem to the cart
    public void addItem(CartItem item) {
        items.add(item);  // Add the item to the list
        item.setCart(this);  // Set the cart reference in the item
    }

    // Method to remove a CartItem from the cart
    public void removeItem(CartItem item) {
        items.remove(item);  // Remove the item from the list
        item.setCart(null);  // Clear the cart reference in the item
    }
}
