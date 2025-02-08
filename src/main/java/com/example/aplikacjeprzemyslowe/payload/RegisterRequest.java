package com.example.aplikacjeprzemyslowe.payload;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

public class RegisterRequest {

    @Size(min = 3, max = 25)
    @NotBlank
    @Getter
    @Setter
    private String fullName;

    @NotBlank
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters.")
    @Getter
    @Setter
    private String username;

    @NotBlank(message = "Password is required.")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$",
            message = "Password must be at least 8 characters, include an uppercase letter, a number, and a special character.")
    @Getter
    @Setter
    private String password;

    @NotBlank(message = "Email is required.")
    @Email(message = "Please provide a valid email address.")
    @Getter
    @Setter
    private String email;
}
