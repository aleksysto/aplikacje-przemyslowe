package com.example.aplikacjeprzemyslowe.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Column(nullable = false)
    @Getter
    @Setter
    private String title;

    // If each book has a single Author (many books to one author)
    @ManyToOne
    @JoinColumn(name = "author_id")  // foreign key column
    @Getter
    @Setter
    private Author author;

    @Column(nullable = false)
    @Getter
    @Setter
    private String genre;

    @Column(length = 2000) // Allows for a longer description if needed
    @Getter
    @Setter
    private String description;

    @Column(name = "publish_date")
    @Getter
    @Setter
    private LocalDate publishDate;  // or use java.util.Date if you prefer

    @Getter
    @Setter
    private String publisher;

    @Getter
    @Setter
    private double rating;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    @Setter
    private List<Comment> comments = new ArrayList<>();

    // or simply: private String comments;

    @Getter
    @Setter
    private boolean borrowed;  // 'true' if borrowed, 'false' if available

    @Column(name = "borrowed_until")
    private LocalDate borrowedUntil;

    @ManyToOne
    @JoinColumn(name = "borrowed_by_id")
    @Getter
    @Setter
    private User borrowedBy;
    // For the image, you can either store a URL/path or the raw bytes.
    // Here, let's store a path or filename:
    private String imageUrl;

    // Constructors
    public Book() {}

    public Book(String title, Author author, String genre, String description, LocalDate publishDate,
                String publisher, double rating, boolean borrowed, LocalDate borrowedUntil,
                String imageUrl) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.description = description;
        this.publishDate = publishDate;
        this.publisher = publisher;
        this.rating = rating;
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
