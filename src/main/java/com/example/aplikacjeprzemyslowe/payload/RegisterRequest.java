package com.example.aplikacjeprzemyslowe.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String username;
    private String password; // ✅ Needed during registration
    private String email;
    private String fullName;
}
