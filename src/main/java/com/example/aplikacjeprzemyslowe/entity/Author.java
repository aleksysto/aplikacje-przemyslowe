package com.example.aplikacjeprzemyslowe.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "authors")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @NotBlank(message = "Author name is required.")
    @Size(max = 100, message = "Author name cannot exceed 100 characters.")
    @Getter
    @Setter
    private String name;

    // If you want a back-reference from Author -> Books:
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    private List<Book> books = new ArrayList<>();

    // Constructors
    public Author() {}

    public Author(String name) {
        this.name = name;
    }

    public void addBook(Book book) {
        books.add(book);
        book.setAuthor(this);
    }

    public void removeBook(Book book) {
        books.remove(book);
        book.setAuthor(null);
    }
}
