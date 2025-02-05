package com.example.aplikacjeprzemyslowe.entity;

import com.example.aplikacjeprzemyslowe.enums.UserType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Column(unique = true, nullable = false)
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters.")
    @Getter
    @Setter
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type")
    @Getter
    @Setter
    private UserType userType = UserType.USER; // Default to USER

    @Getter
    @Setter
    private String password;

    @Getter
    @Setter
    @NotBlank(message = "Email is required.")
    @Email(message = "Please provide a valid email address.")
    @Size(max = 255, message = "Email cannot exceed 255 characters.")
    private String email;

    @Getter
    @Setter
    @Size(max = 100, message = "Full name cannot exceed 100 characters.")
    private String fullName;

    @OneToMany(mappedBy = "borrowedBy", cascade = CascadeType.ALL)
    private List<Book> borrowedBooks = new ArrayList<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    @Setter
    @Getter
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter
    @Setter
    private List<BookRating> bookRatings = new ArrayList<>();


    public User() {
    }

    public User(String username, String password, String email, String fullName) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
    }

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
