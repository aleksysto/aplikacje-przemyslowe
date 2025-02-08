package com.example.aplikacjeprzemyslowe.payload;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

public class RatingRequest {
    @Min(value = 1, message = "Rating must be at least 1.")
    @Max(value = 5, message = "Rating must not exceed 5.")
    @Getter
    @Setter
    private int rating;
}
