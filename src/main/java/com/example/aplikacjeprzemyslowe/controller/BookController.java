package com.example.aplikacjeprzemyslowe.controller;

import com.example.aplikacjeprzemyslowe.entity.Book;
import com.example.aplikacjeprzemyslowe.entity.BookRating;
import com.example.aplikacjeprzemyslowe.entity.CustomUserDetails;
import com.example.aplikacjeprzemyslowe.entity.User;
import com.example.aplikacjeprzemyslowe.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/search/author")
    public List<Book> searchByAuthor(@RequestParam String author, @RequestParam(defaultValue="false") boolean onlyAvailable) {
        return bookService.searchBooksByAuthor(author, onlyAvailable);
    }

    @GetMapping("/search/title")
    public List<Book> searchByTitle(@RequestParam String title, @RequestParam(defaultValue="false") boolean onlyAvailable) {
        return bookService.searchBooksByTitle(title, onlyAvailable);
    }

    @GetMapping("/search/genre")
    public List<Book> searchByGenre(@RequestParam String genre, @RequestParam(defaultValue="false") boolean onlyAvailable) {
        return bookService.searchBooksByGenre(genre, onlyAvailable);
    }

    @GetMapping("/top-rated")
    public List<Book> getTop5(@RequestParam(defaultValue="false") boolean onlyAvailable) {
        return bookService.getTop5ByAverageRating(onlyAvailable);
    }
    @GetMapping("/{bookId}/ratings/count")
    public int getBookRatingCount(@PathVariable Long bookId) {
        return bookService.getNumberOfRatings(bookId);
    }
    @GetMapping("/{bookId}")
    public ResponseEntity<Book> getBook(@PathVariable Long bookId) {
        return bookService.getBookById(bookId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{bookId}/details")
    public ResponseEntity<?> getBookDetails(@PathVariable Long bookId) {
        Optional<Book> optionalBook = bookService.getBookDetails(bookId);

        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            return ResponseEntity.ok(book); // Return the book properly
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found");
        }
    }

    @GetMapping("/genres")
    public List<String> getAllGenres() {
        return bookService.getAllGenres();
    }
    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Book> addBook(@RequestBody Book book) {
        Book savedBook = bookService.saveBook(book);
        return new ResponseEntity<>(savedBook, HttpStatus.CREATED);
    }

    // Update an existing book (Admin only)
    @PutMapping("/admin/{bookId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Book> updateBook(@PathVariable Long bookId, @RequestBody Book updatedBook) {
        Book savedBook = bookService.updateBook(bookId, updatedBook);
        return ResponseEntity.ok(savedBook);
    }

    // Delete a book (Admin only)
    @DeleteMapping("/admin/{bookId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBook(@PathVariable Long bookId) {
        bookService.deleteBook(bookId);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/{bookId}/borrow")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Book> borrowBook(@PathVariable Long bookId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Optional<Book> book = bookService.borrowBook(bookId, customUserDetails);
        return book.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{bookId}/return")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Book> returnBook(@PathVariable Long bookId, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Optional<Book> book = bookService.returnBook(bookId, customUserDetails);
        return book.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
    @PutMapping("/{bookId}/rate")
     @PreAuthorize("isAuthenticated()")
    public ResponseEntity<BookRating> rateBook(@PathVariable Long bookId,
                                               @RequestParam int rating,
                                               @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if (customUserDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        Long userId = customUserDetails.getId();

        if (rating < 1 || rating > 5) {
            return ResponseEntity.badRequest().body(null);
        }

        BookRating bookRating = bookService.rateBook(bookId, userId, rating);
        return ResponseEntity.ok(bookRating);
    }


    @DeleteMapping("/{bookId}/rate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> removeRating(@PathVariable Long bookId, @AuthenticationPrincipal CustomUserDetails user) {
        bookService.removeRating(bookId, user.getId());
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{bookId}/ratings/average")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long bookId) {
        Double averageRating = bookService.getAverageRating(bookId);
        return ResponseEntity.ok(averageRating);
    }
}
