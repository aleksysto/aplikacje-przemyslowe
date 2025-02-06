package com.example.aplikacjeprzemyslowe.service;

import com.example.aplikacjeprzemyslowe.entity.Book;
import com.example.aplikacjeprzemyslowe.entity.BookRating;
import com.example.aplikacjeprzemyslowe.entity.User;
import com.example.aplikacjeprzemyslowe.repository.BookRatingRepository;
import com.example.aplikacjeprzemyslowe.repository.BookRepository;
import com.example.aplikacjeprzemyslowe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final BookRatingRepository bookRatingRepository;
    private final UserRepository userRepository;

    @Autowired
    public BookService(BookRepository bookRepository,
                       BookRatingRepository bookRatingRepository,
                       UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.bookRatingRepository = bookRatingRepository;
        this.userRepository = userRepository;
    }
    public BookRating rateBook(Long bookId, Long userId, int ratingValue) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Optional<BookRating> existingRating = bookRatingRepository.findByBookAndUser(bookId, userId);

        if (existingRating.isPresent()) {
            BookRating rating = existingRating.get();
            rating.setRating(ratingValue);
            return bookRatingRepository.save(rating);
        } else {
            BookRating newRating = new BookRating(book, user, ratingValue);
            return bookRatingRepository.save(newRating);
        }
    }

    public void removeRating(Long bookId, Long userId) {
        Optional<BookRating> existingRating = bookRatingRepository.findByBookAndUser(bookId, userId);
        existingRating.ifPresent(bookRatingRepository::delete);
    }

    public Double getAverageRating(Long bookId) {
        return bookRatingRepository.findAverageRatingByBook(bookId);
    }

    public List<Book> searchBooksByAuthor(String authorName, boolean onlyAvailable) {
        if (onlyAvailable) {
            return bookRepository.findAvailableByAuthorNameContains(authorName);
        } else {
            return bookRepository.findByAuthorNameContains(authorName);
        }
    }

    public List<Book> searchBooksByTitle(String title, boolean onlyAvailable) {
        if (onlyAvailable) {
            return bookRepository.findAvailableByTitleContains(title);
        } else {
            return bookRepository.findByTitleContains(title);
        }
    }

    public List<Book> searchBooksByGenre(String genre, boolean onlyAvailable) {
        if (onlyAvailable) {
            return bookRepository.findAvailableByGenreContains(genre);
        } else {
            return bookRepository.findByGenreContains(genre);
        }
    }

    public List<Book> getTop5ByAverageRating(boolean onlyAvailable) {
        List<Book> orderedBooks = bookRepository.findTopByAverageRating();

        if (onlyAvailable) {
            orderedBooks.removeIf(Book::isBorrowed);
        }

        // return top 5
        return orderedBooks.stream().limit(5).toList();
    }
    public int getNumberOfRatings(Long bookId) {
        return bookRepository.countRatingsForBook(bookId);
    }
    public Optional<Book> getBookById(Long bookId) {
        return bookRepository.findById(bookId);
    }
    public Optional<Book> getBookDetails(Long bookId) {
        return bookRepository.findBookWithDetails(bookId);
    }
    public List<String> getAllGenres() {
        return bookRepository.findAllGenres();
    }
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    public Book updateBook(Long bookId, Book updatedBook) {
        return bookRepository.findById(bookId)
                .map(existingBook -> {
                    existingBook.setTitle(updatedBook.getTitle());
                    existingBook.setAuthor(updatedBook.getAuthor());
                    existingBook.setGenre(updatedBook.getGenre());
                    existingBook.setDescription(updatedBook.getDescription());
                    existingBook.setPublishDate(updatedBook.getPublishDate());
                    existingBook.setPublisher(updatedBook.getPublisher());
                    return bookRepository.save(existingBook);
                })
                .orElseThrow(() -> new RuntimeException("Book not found"));
    }

    public void deleteBook(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new RuntimeException("Book not found");
        }
        bookRepository.deleteById(bookId);
    }
    public Optional<Book> borrowBook(Long bookId, User user) {
        return bookRepository.findById(bookId).map(book -> {
            if (book.isBorrowed()) {
                throw new RuntimeException("Book is already borrowed.");
            }
            book.setBorrowed(true);
            book.setBorrowedUntil(LocalDate.now().plusWeeks(2));
            book.setBorrowedBy(user);
            return bookRepository.save(book);
        });
    }

    public Optional<Book> returnBook(Long bookId, User user) {
        return bookRepository.findById(bookId).map(book -> {
            if (!book.isBorrowed()) {
                throw new RuntimeException("Book is not currently borrowed.");
            }
            if (!book.getBorrowedBy().equals(user)) {
                throw new RuntimeException("Not authorized to return this book.");
            }
            book.setBorrowed(false);
            book.setBorrowedUntil(null);
            book.setBorrowedBy(null);
            return bookRepository.save(book);
        });
    }
}
