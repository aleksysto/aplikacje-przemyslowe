package com.example.aplikacjeprzemyslowe.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public class EditBookRequest {
    @Size(max = 200, message = "Title cannot exceed 200 characters.")
    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private Long authorId;

    @Size(max = 100, message = "Genre cannot exceed 100 characters.")
    @Getter
    @Setter
    private String genre;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters.")
    @Getter
    @Setter
    private String description;

    @PastOrPresent(message = "Publish date cannot be in the future.")
    @Getter
    @Setter
    private LocalDate publishDate;

    @Getter
    @Setter
    @Size(max = 100, message = "Publisher name cannot exceed 100 characters.")
    private String publisher;

    @Getter
    @Setter
    private boolean borrowed = false;

    @Getter
    @Setter
    private LocalDate borrowedUntil = null;

    private String imageUrl;

    public EditBookRequest() {}

    public EditBookRequest(String title, Long authorId, String genre, String description, LocalDate publishDate,
                       String publisher, boolean borrowed, LocalDate borrowedUntil,
                       String imageUrl) {
        this.title = title;
        this.authorId = authorId;
        this.genre = genre;
        this.description = description;
        this.publishDate = publishDate;
        this.publisher = publisher;
        this.borrowed = borrowed;
        this.borrowedUntil = borrowedUntil;
        this.imageUrl = imageUrl;
    }
}
