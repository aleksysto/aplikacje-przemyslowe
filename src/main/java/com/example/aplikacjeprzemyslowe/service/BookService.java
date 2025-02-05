package com.example.aplikacjeprzemyslowe.service;

import com.example.aplikacjeprzemyslowe.entity.Book;
import com.example.aplikacjeprzemyslowe.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
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

}
