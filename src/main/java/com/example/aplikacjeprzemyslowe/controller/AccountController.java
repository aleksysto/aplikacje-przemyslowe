package com.example.aplikacjeprzemyslowe.controller;

import com.example.aplikacjeprzemyslowe.entity.Book;
import com.example.aplikacjeprzemyslowe.entity.CustomUserDetails;
import com.example.aplikacjeprzemyslowe.entity.User;
import com.example.aplikacjeprzemyslowe.payload.EditUserRequest;
import com.example.aplikacjeprzemyslowe.payload.LoginRequest;
import com.example.aplikacjeprzemyslowe.payload.RegisterRequest;
import com.example.aplikacjeprzemyslowe.service.BookService;
import com.example.aplikacjeprzemyslowe.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/auth")
public class AccountController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final BookService bookService;

    @Autowired
    public AccountController(UserService userService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, BookService bookService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.bookService = bookService;
    }
    // Show login page
    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequest()); // Ensure loginRequest is available
        return "auth/login";
    }

    // Show registration page
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";  // This refers to register.html template
    }
    @PostMapping("/register")
    public String validateAndRegisterUser(
            @Valid @ModelAttribute("registerRequest") RegisterRequest registerRequest,
            BindingResult bindingResult, Model model) {

        if (userService.findByUsername(registerRequest.getUsername()).isPresent()) {
            bindingResult.rejectValue("username", "error.registerRequest", "Username already taken.");
        }
        if (userService.findByEmail(registerRequest.getEmail())) {
            bindingResult.rejectValue("email", "error.registerRequest", "Email is already in use.");
        }
        if (!userService.isValidPassword(registerRequest.getPassword())) {
            bindingResult.rejectValue("password", "error.registerRequest", "Password must be at least 8 characters long, contain an uppercase letter, a number, and a special character.");
        }

        // If there are validation errors, return to the registration page
        if (bindingResult.hasErrors()) {
            model.addAttribute("registerRequest", registerRequest);
            return "auth/register";
        }

        // If validation passes, register the user
        userService.registerNewUser(new User(
                registerRequest.getUsername(),
                registerRequest.getPassword(),
                registerRequest.getEmail(),
                registerRequest.getFullName()
        ));

        return "redirect:/auth/login?success";
    }
    @PostMapping("/login")
    public String loginUser(@Valid @ModelAttribute("loginRequest") LoginRequest loginRequest,
                            BindingResult bindingResult) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return "redirect:/home"; // Redirect to homepage after successful login
        } catch (Exception e) {
            bindingResult.rejectValue("password", "error.loginRequest", "Wrong username or password");
            return "auth/login"; // Stay on login page with error message
        }
    }
    @GetMapping("/profile")
    public String accountPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        User user = userService.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Book> borrowedBooks = bookService.getBorrowedBooks(user);

        model.addAttribute("user", user);
        model.addAttribute("registerRequest", new EditUserRequest());
        model.addAttribute("borrowedBooks", borrowedBooks);

        return "auth/profile";
    }

    @PostMapping("/update")
    public String updateUser(@AuthenticationPrincipal CustomUserDetails userDetails,
                             @Valid @ModelAttribute("registerRequest") EditUserRequest userUpdateRequest,
                             BindingResult bindingResult, Model model) {
        User existingUser = userService.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (userUpdateRequest.getUsername() != null && !userUpdateRequest.getUsername().isEmpty()) {
            if (userUpdateRequest.getUsername().length() > 20 || userUpdateRequest.getUsername().length() < 3) {
                bindingResult.rejectValue("username", "error.userUpdateRequest", "Username must be between 3 and 20 characters");
            } else {
                existingUser.setUsername(userUpdateRequest.getUsername());
            }
        }
        if (userUpdateRequest.getFullName() != null && !userUpdateRequest.getFullName().isEmpty()) {
            if (userUpdateRequest.getFullName().length() > 25 || userUpdateRequest.getFullName().length() < 3) {
                bindingResult.rejectValue("fullName", "error.userUpdateRequest", "Full name must be between 3 and 25 characters");
            } else {
                existingUser.setFullName(userUpdateRequest.getFullName());
            }
        }
        if (userUpdateRequest.getEmail() != null && !userUpdateRequest.getEmail().isEmpty()) {
            if (!existingUser.getEmail().equals(userUpdateRequest.getEmail()) && userService.findByEmail(userUpdateRequest.getEmail())) {
                if (!userService.isValidEmail(userUpdateRequest.getEmail())) {
                    bindingResult.rejectValue("email", "error.userUpdateRequest", "Email must be a valid email");
                } else {
                    bindingResult.rejectValue("email", "error.userUpdateRequest", "Email is already in use.");
                }
            } else {
                existingUser.setEmail(userUpdateRequest.getEmail());
            }
        }
        if (userUpdateRequest.getPassword() != null && !userUpdateRequest.getPassword().isEmpty()) {
            if (!userService.isValidPassword(userUpdateRequest.getPassword())) {
                bindingResult.rejectValue("password", "error.userUpdateRequest", "Password must be at least 8 characters long, contain an uppercase letter, a number, and a special character.");
            } else {
                existingUser.setPassword(passwordEncoder.encode(userUpdateRequest.getPassword()));
            }
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", existingUser);
            List<Book> borrowedBooks = bookService.getBorrowedBooks(existingUser);
            model.addAttribute("user", existingUser);
            model.addAttribute("registerRequest", userUpdateRequest);
            model.addAttribute("borrowedBooks", borrowedBooks);
            return "auth/profile";
        }

        userService.updateUser(userDetails.getId(), existingUser);
        return "redirect:/auth/profile";
    }


    @PostMapping("/delete")
    public String deleteUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.deleteUser(userDetails.getId());
        return "redirect:/";
    }
}
