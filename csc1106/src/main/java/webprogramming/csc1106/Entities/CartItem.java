package webprogramming.csc1106.Entities;

import jakarta.persistence.*;

// Entity class representing an item in the shopping cart
@Entity
@Table(name = "cart_item")
public class CartItem {

    // Primary key of the CartItem table
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // Many-to-one relationship with the UploadCourse entity
    // Each CartItem is associated with one UploadCourse
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private UploadCourse course;

    // Many-to-one relationship with the Cart entity
    // Each CartItem is associated with one Cart
    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    // Default constructor
    public CartItem() {
    }

    // Parameterized constructor to initialize a CartItem with a course and a cart
    public CartItem(UploadCourse course, Cart cart) {
        this.course = course;
        this.cart = cart;
    }

    // Getter and setter methods for the id field
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Getter and setter methods for the course field
    public UploadCourse getCourse() {
        return course;
    }

    public void setCourse(UploadCourse course) {
        this.course = course;
    }

    // Getter and setter methods for the cart field
    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    // Override the toString method to provide a string representation of the CartItem object
    // This is useful for debugging purposes to check if the fields are being set correctly
    @Override
    public String toString() {
        return "CartItem{" +
                "id=" + id +
                ", course=" + course.getTitle() +
                ", cart=" + cart.getId() +
                '}';
    }
}
