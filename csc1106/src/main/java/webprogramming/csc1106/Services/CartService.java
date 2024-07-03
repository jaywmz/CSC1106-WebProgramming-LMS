package webprogramming.csc1106.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webprogramming.csc1106.Entities.*;
import webprogramming.csc1106.Repositories.CartItemRepository;
import webprogramming.csc1106.Repositories.CartRepository;
import webprogramming.csc1106.Repositories.UploadCourseRepository;

import java.util.Optional;
import java.util.logging.Logger;

// Service class for managing cart operations
@Service
public class CartService {

    // Logger for logging messages
    private static final Logger logger = Logger.getLogger(CartService.class.getName());

    // Autowired repositories for accessing database
    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UploadCourseRepository uploadCourseRepository;

    // Method to get the current cart
    public Cart getCart() {
        // Retrieve the cart with ID 1, or create a new one if it doesn't exist
        Cart cart = cartRepository.findById(1L).orElseGet(() -> {
            Cart newCart = new Cart();
            return cartRepository.save(newCart);
        });

        return cart;
    }

    // Method to add a course to the cart
    public void addCourseToCart(Long courseId) {
        // Find the course by its ID
        UploadCourse course = uploadCourseRepository.findById(courseId)
                .orElseThrow(() -> {
                    logger.severe("Invalid course Id: " + courseId);
                    return new IllegalArgumentException("Invalid course Id: " + courseId);
                });

        // Get the current cart
        Cart cart = getCart();

        // Create a new CartItem with the course and cart
        CartItem item = new CartItem();
        item.setCourse(course);
        item.setCart(cart);

        // Log the addition of the item to the cart
        logger.info("Adding item to cart: " + item.toString());

        // Add the item to the cart, save the item and the cart in the repository
        cart.addItem(item);
        cartItemRepository.save(item);
        cartRepository.save(cart);
    }

    // Method to remove a course from the cart
    public void removeCourseFromCart(Long courseId) {
        // Get the current cart
        Cart cart = getCart();

        // Find the item to remove by its course ID
        Optional<CartItem> itemToRemove = cart.getItems().stream()
                .filter(item -> item.getCourse().getId().equals(courseId))
                .findFirst();

        // Remove the item from the cart if found
        itemToRemove.ifPresent(cart::removeItem);

        // Save the updated cart in the repository
        cartRepository.save(cart);
    }
}
