package com.example.aplikacjeprzemyslowe.controller;

import com.example.aplikacjeprzemyslowe.entity.User;
import com.example.aplikacjeprzemyslowe.payload.LoginRequest;
import com.example.aplikacjeprzemyslowe.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Validated @RequestBody User userRequest) {
        try {
            // Create new user
            User savedUser = userService.registerNewUser(userRequest);
            return ResponseEntity.ok(savedUser);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest,
                                       HttpServletRequest request) {
        try {
            // 1. Create an authentication token from the login request
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    );

            // 2. Attempt to authenticate
            Authentication authentication = authenticationManager.authenticate(authToken);

            // 3. If successful, set context and create HTTP session
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Ensure session is created. (true) means: create a session if one doesn't exist
            HttpSession session = request.getSession(true);
            // Spring Security stores the SecurityContext in the session (if session-based)
            session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

            return ResponseEntity.ok("Login successful!");

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        } catch (DisabledException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is disabled");
        } catch (LockedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is locked");
        } catch (AuthenticationException ex) {
            // Catch any other Spring Security exceptions
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
        }
    }
    @DeleteMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #user.id == #userId")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId, @AuthenticationPrincipal User user) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
