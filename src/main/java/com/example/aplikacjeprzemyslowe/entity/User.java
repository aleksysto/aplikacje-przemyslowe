package com.example.aplikacjeprzemyslowe.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // For login, you might store username (or email) + password.
    @Column(unique = true, nullable = false)
    @Getter
    @Setter
    private String username;

    // In real apps, you should store passwords securely (hashed + salted).
    @Getter
    @Setter
    private String password;

    // If you want to store e.g. name, email, etc.:
    @Getter
    @Setter
    private String email;
    @Getter
    @Setter
    private String fullName;

    /*
     * One user can borrow multiple books.
     * We’ll map this from the Book side as well (Book#borrowedBy).
     */
    @OneToMany(mappedBy = "borrowedBy", cascade = CascadeType.ALL)
    private List<Book> borrowedBooks = new ArrayList<>();

    /*
     * One user can also leave multiple comments.
     * We'll map this from Comment#author as well.
     */
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    // Constructors
    public User() {
    }

    public User(String username, String password, String email, String fullName) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
    }

    // Getters & setters

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // ... other getters / setters ...

    public List<Book> getBorrowedBooks() {
        return borrowedBooks;
    }

    public List<Comment> getComments() {
        return comments;
    }

    // Helper methods to keep both sides in sync if needed
    public void addBorrowedBook(Book book) {
        this.borrowedBooks.add(book);
        book.setBorrowedBy(this);
    }

    public void removeBorrowedBook(Book book) {
        this.borrowedBooks.remove(book);
        book.setBorrowedBy(null);
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
        comment.setAuthor(this);
    }

    public void removeComment(Comment comment) {
        this.comments.remove(comment);
        comment.setAuthor(null);
    }
}
