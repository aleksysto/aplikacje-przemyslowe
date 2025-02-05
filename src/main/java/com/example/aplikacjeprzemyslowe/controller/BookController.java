package com.example.aplikacjeprzemyslowe.controller;

import com.example.aplikacjeprzemyslowe.entity.Book;
import com.example.aplikacjeprzemyslowe.service.BookService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
