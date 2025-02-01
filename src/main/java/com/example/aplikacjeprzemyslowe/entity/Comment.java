package com.example.aplikacjeprzemyslowe.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    private String text;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @Getter
    @Setter
    private LocalDateTime createdAt = LocalDateTime.now();

    // link back to Book
    @ManyToOne
    @JoinColumn(name = "book_id")
    @Getter
    @Setter
    private Book book;

    public Comment() {}

    public Comment(String text, Book book) {
        this.text = text;
        this.book = book;
    }

    // getters, setters, etc.
}
