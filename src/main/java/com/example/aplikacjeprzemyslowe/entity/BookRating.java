package com.example.aplikacjeprzemyslowe.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "book_ratings",
        uniqueConstraints = {
                // Optionally ensure one user can only have one rating per book
                @UniqueConstraint(columnNames = {"user_id", "book_id"})
        })
public class BookRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    // Each rating belongs to one Book
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    @Getter
    @Setter
    @JsonIgnore
    private Book book;

    // The user who gave the rating
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @Getter
    @Setter
    @JsonIgnore
    private User user;

    // The rating itself, let’s say 1..5 or 1..10
    @Min(value = 1, message = "Rating must be at least 1.")
    @Max(value = 5, message = "Rating must not exceed 5.")
    @Getter
    @Setter
    private int rating;

    public BookRating() {}

    public BookRating(Book book, User user, int rating) {
        this.book = book;
        this.user = user;
        this.rating = rating;
    }

}
