package webprogramming.csc1106.Entities;

import jakarta.persistence.*;

// CartItem.java
@Entity
@Table(name = "cart_item")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private UploadCourse course;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @Column(name = "price", nullable = false)
    private Double price; // Storing the price separately ensures the price at the time of adding to cart is recorded, allowing for price changes in the future without affecting the cart items.

    @Transient
    private String lecturer; // Transient field for the lecturer's name, not stored in the database.

    @Transient
    private String categoryName; // Transient field for the category name, not stored in the database.

    // Constructors
    public CartItem() {
    }

    public CartItem(UploadCourse course, Cart cart, Double price) {
        this.course = course;
        this.cart = cart;
        this.price = price;
    }

    // Getters and setters...
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UploadCourse getCourse() {
        return course;
    }

    public void setCourse(UploadCourse course) {
        this.course = course;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getLecturer() {
        return lecturer;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    // Debugging tool to check if the fields are being set correctly
    @Override
    public String toString() {
        return "CartItem{" +
                "id=" + id +
                ", course=" + course.getTitle() +
                ", cart=" + cart.getId() +
                ", price=" + price +
                ", lecturer='" + lecturer + '\'' +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }
}
