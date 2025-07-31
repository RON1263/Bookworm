package com.example.controller;

import com.example.model.*;
import com.example.service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final IUser userService;
    private final IShelf shelfService;
    private final ICartService cartService;

    @Autowired
    public AuthController(UserService userService, ShelfService shelfService, CartService cartService) {
        this.userService = userService;
        this.shelfService = shelfService;
        this.cartService = cartService;
    }

    // ---------- SIGN UP ----------
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody User user) {
        if (userService.existsByEmail(user.getUserEmail())) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        if (userService.existsByPhone(user.getUserPhone())) {
            return ResponseEntity.badRequest().body("Phone already exists");
        }

        // Save user
        User savedUser = userService.createUser(user);

        // Create and assign Shelf
        Shelf shelf = new Shelf();
        shelf.setUser(savedUser);
        shelfService.createOrUpdateShelf(shelf);

        // Create and assign Cart
        Cart cart = new Cart();
        cart.setUser(savedUser);
        cartService.saveCart(cart);

        return ResponseEntity.ok("User registered successfully with shelf and cart");
    }

    // ---------- LOGIN ----------
    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session) {

        Optional<User> userOpt = userService.getUserByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body("Invalid email or password");
        }

        User user = userOpt.get();

        if (!password.equals(user.getUserPassword())) {
            return ResponseEntity.status(401).body("Invalid email or password");
        }

        // Store user in session
        session.setAttribute("user", user);

        return ResponseEntity.ok("Login successful. User stored in session.");
    }
    
    
 

 // ---------- LOGOUT ----------
		 @PostMapping("/logout")
		 public ResponseEntity<String> logout(HttpSession session) {
		     session.invalidate(); // ends the current user session
		     return ResponseEntity.ok("Logout successful. Session invalidated.");
		 }
		 //hellobyShashank

}
