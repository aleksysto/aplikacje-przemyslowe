package com.example.aplikacjeprzemyslowe.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

public class AuthorRequest {
    @NotBlank(message = "Author name is required.")
    @Size(max = 100, message = "Author name cannot exceed 100 characters.")
    @Getter
    @Setter
    private String name;
}
