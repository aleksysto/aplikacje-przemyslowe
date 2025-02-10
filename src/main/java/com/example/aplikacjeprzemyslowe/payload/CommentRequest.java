package com.example.aplikacjeprzemyslowe.payload;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

public class CommentRequest {
    @Getter
    @Setter
    @Size(min = 3, max = 2000, message = "Comment must be between 3 and 2000 chars long")
    private String text;

}
