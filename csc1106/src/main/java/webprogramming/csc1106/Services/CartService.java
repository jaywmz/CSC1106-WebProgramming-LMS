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

    public void addCourseToCart(UploadCourse course) {
        Cart cart = getCart();
        CartItem item = new CartItem();
        item.setCourse(course);
        item.setCart(cart);
        item.setPrice(course.getPrice()); // Set the price field
        cart.addItem(item);
        cartItemRepository.save(item);
        cartRepository.save(cart);
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
