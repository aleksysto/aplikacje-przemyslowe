package com.example.aplikacjeprzemyslowe.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Getter
    @Setter
    @NotBlank(message = "Comment cannot be empty.")
    @Size(max = 1000, message = "Comment cannot exceed 1000 characters.")
    private String text;

    @ManyToOne
    @JoinColumn(name = "author_id")
    @Getter
    @Setter
    private User author;

    @Getter
    @Setter
    private LocalDateTime createdAt = LocalDateTime.now();

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

}
