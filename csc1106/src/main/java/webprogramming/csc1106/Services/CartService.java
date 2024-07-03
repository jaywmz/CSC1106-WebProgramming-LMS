// CartService.java
package webprogramming.csc1106.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webprogramming.csc1106.Entities.*;
import webprogramming.csc1106.Repositories.CartItemRepository;
import webprogramming.csc1106.Repositories.CartRepository;
import webprogramming.csc1106.Repositories.UploadCourseRepository;

import java.util.Optional;
import java.util.logging.Logger;

@Service
public class CartService {

    private static final Logger logger = Logger.getLogger(CartService.class.getName());

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private UploadCourseRepository uploadCourseRepository;

    public Cart getCart() {
        Cart cart = cartRepository.findById(1L).orElseGet(() -> {
            Cart newCart = new Cart();
            return cartRepository.save(newCart);
        });

        // Populate transient fields
        for (CartItem item : cart.getItems()) {
            item.setLecturer(item.getCourse().getLecturer());
            if (!item.getCourse().getCourseCategories().isEmpty()) {
                item.setCategoryName(item.getCourse().getCourseCategories().get(0).getCategoryGroup().getName());
            }
        }

        return cart;
    }

    public void addCourseToCart(Long courseId) {
        UploadCourse course = uploadCourseRepository.findById(courseId)
                .orElseThrow(() -> {
                    logger.severe("Invalid course Id: " + courseId);
                    return new IllegalArgumentException("Invalid course Id: " + courseId);
                });

        Cart cart = getCart();
        CartItem item = new CartItem();
        item.setCourse(course);
        item.setCart(cart);
        item.setPrice(course.getPrice());
        item.setLecturer(course.getLecturer());

        // Set the category name
        if (!course.getCourseCategories().isEmpty()) {
            item.setCategoryName(course.getCourseCategories().get(0).getCategoryGroup().getName());
            logger.info("Setting category name: " + course.getCourseCategories().get(0).getCategoryGroup().getName());
        } else {
            logger.warning("No categories found for course: " + courseId);
        }

        logger.info("Adding item to cart: " + item.toString());
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
