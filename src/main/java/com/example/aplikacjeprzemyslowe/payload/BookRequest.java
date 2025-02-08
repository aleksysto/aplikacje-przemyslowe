package com.example.aplikacjeprzemyslowe.payload;

import com.example.aplikacjeprzemyslowe.entity.Author;
import com.example.aplikacjeprzemyslowe.entity.BookRating;
import com.example.aplikacjeprzemyslowe.entity.Comment;
import com.example.aplikacjeprzemyslowe.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class BookRequest {
    @Size(max = 200, message = "Title cannot exceed 200 characters.")
    @Getter
    @Setter
    @NotBlank
    private String title;

    @Getter
    @Setter
    private Long authorId;

    @Size(max = 100, message = "Genre cannot exceed 100 characters.")
    @Getter
    @Setter
    @NotBlank
    private String genre;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters.")
    @Getter
    @Setter
    @NotBlank
    private String description;

    @PastOrPresent(message = "Publish date cannot be in the future.")
    @Getter
    @Setter
    private LocalDate publishDate;

    @Getter
    @Setter
    @Size(max = 100, message = "Publisher name cannot exceed 100 characters.")
    @NotBlank
    private String publisher;

    @Getter
    @Setter
    private boolean borrowed = false;

    @Getter
    @Setter
    private LocalDate borrowedUntil = null;

    private String imageUrl;

    public BookRequest() {}

    public BookRequest(String title, Long authorId, String genre, String description, LocalDate publishDate,
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
