package webprogramming.csc1106.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webprogramming.csc1106.Entities.*;
import webprogramming.csc1106.Repositories.CartItemRepository;
import webprogramming.csc1106.Repositories.CartRepository;
import webprogramming.csc1106.Repositories.UploadCourseRepository;

import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UploadCourseRepository uploadCourseRepository;

    public Cart getCart() {
        // Assuming a single cart for simplicity, you might need to associate with a user
        return cartRepository.findById(1L).orElseGet(() -> {
            Cart newCart = new Cart();
            return cartRepository.save(newCart);
        });
    }

    public String addCourseToCart(UploadCourse course) {
        // Check if the course already exists in the cart
        Cart cart = getCart();
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getCourse().getId().equals(course.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            // Course already in cart, return a message
            return "Course '" + course.getTitle() + "' is already in your cart.";
        } else {
            // Add new item to cart
            CartItem newItem = new CartItem();
            newItem.setCourse(course);
            newItem.setCart(cart);
            newItem.setPrice(course.getPrice()); // Set the price field
            newItem.setLecturer(course.getLecturer()); // Set the lecturer field from UploadCourse

            cart.addItem(newItem);
            cartItemRepository.save(newItem);
            cartRepository.save(cart);
            return "Course '" + course.getTitle() + "' added to your cart successfully.";
        }
    }

    public void removeCourseFromCart(Long courseId) {
        Cart cart = getCart();
        Optional<CartItem> itemToRemove = cart.getItems().stream()
                .filter(item -> item.getCourse().getId().equals(courseId))
                .findFirst();
        itemToRemove.ifPresent(cart::removeItem);
        cartRepository.save(cart);
    }
}
