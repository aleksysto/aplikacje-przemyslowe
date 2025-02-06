package com.example.aplikacjeprzemyslowe.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Title is required.")
    @Size(max = 200, message = "Title cannot exceed 200 characters.")
    @Getter
    @Setter
    private String title;

    @ManyToOne
    @JoinColumn(name = "author_id")
    @Getter
    @Setter
    private Author author;

    @Column(nullable = false)
    @Size(max = 100, message = "Genre cannot exceed 100 characters.")
    @Getter
    @Setter
    private String genre;

    @Column(length = 2000)
    @Size(max = 2000, message = "Description cannot exceed 2000 characters.")
    @Getter
    @Setter
    private String description;

    @Column(name = "publish_date")
    @PastOrPresent(message = "Publish date cannot be in the future.")
    @Getter
    @Setter
    private LocalDate publishDate;

    @Getter
    @Setter
    @Size(max = 100, message = "Publisher name cannot exceed 100 characters.")
    private String publisher;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    @Setter
    private Set<BookRating> ratings = new HashSet<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    @Setter
    private Set<Comment> comments = new HashSet<>();


    public void addRating(BookRating rating) {
        ratings.add(rating);
        rating.setBook(this);
    }

    public void removeRating(BookRating rating) {
        ratings.remove(rating);
        rating.setBook(null);
    }

    @Getter
    @Setter
    private boolean borrowed;

    @Column(name = "borrowed_until")
    @Getter
    @Setter
    private LocalDate borrowedUntil;

    @ManyToOne
    @JoinColumn(name = "borrowed_by_id")
    @Getter
    @Setter
    private User borrowedBy;

    private String imageUrl;

    public Book() {}

    public Book(String title, Author author, String genre, String description, LocalDate publishDate,
                String publisher, boolean borrowed, LocalDate borrowedUntil,
                String imageUrl) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.description = description;
        this.publishDate = publishDate;
        this.publisher = publisher;
        this.borrowed = borrowed;
        this.borrowedUntil = borrowedUntil;
        this.imageUrl = imageUrl;
    }

    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setBook(this);
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);
        comment.setBook(null);
    }
}
