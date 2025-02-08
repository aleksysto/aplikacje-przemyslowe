package com.example.aplikacjeprzemyslowe.controller;

import com.example.aplikacjeprzemyslowe.entity.*;
import com.example.aplikacjeprzemyslowe.payload.AuthorRequest;
import com.example.aplikacjeprzemyslowe.payload.BookRequest;
import com.example.aplikacjeprzemyslowe.payload.CommentRequest;
import com.example.aplikacjeprzemyslowe.payload.EditBookRequest;
import com.example.aplikacjeprzemyslowe.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final BookService bookService;
    private final AuthorService authorService;
    private final UserService userService;
    private final CommentService commentService;

    @Autowired
    public AdminController(BookService bookService, AuthorService authorService, UserService userService, CommentService commentService) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.userService = userService;
        this.commentService = commentService;
    }

    @GetMapping
    public String adminDashboard(Model model) {
        model.addAttribute("books", bookService.getAllBooks());
        model.addAttribute("authors", authorService.getAllAuthors());
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("comments", commentService.getAllComments());
        return "admin/admin-dashboard";
    }

    @GetMapping("/books/add")
    public String showAddBookPage(Model model) {
        model.addAttribute("bookRequest", new BookRequest());
        model.addAttribute("authors", authorService.getAllAuthors());
        return "admin/add-book";
    }
    @PostMapping("/books/add")
    public String addBook(@ModelAttribute("bookRequest") @Valid BookRequest bookRequest, BindingResult result, Model model) {
        if (result.hasErrors()) {
            System.out.println(result.getAllErrors());
            model.addAttribute("bookRequest", bookRequest);
            model.addAttribute("authors", authorService.getAllAuthors());
            return "admin/add-book";
        }
        bookService.addBook(bookRequest);
        return "redirect:/admin?success";
    }


    @PostMapping("/books/delete/{bookId}")
    public String deleteBook(@PathVariable Long bookId) {
        bookService.deleteBook(bookId);
        return "redirect:/admin?success";
    }

    @GetMapping("/authors/add")
    public String showAddAuthorPage(Model model) {
        model.addAttribute("authorRequest", new AuthorRequest());
        return "admin/add-author";
    }
    @PostMapping("/authors/add")
    public String addAuthor(@ModelAttribute("authorRequest") @Valid AuthorRequest author, BindingResult result, Model model) {
        if (result.hasErrors()) {
            System.out.println(result.getAllErrors());
            model.addAttribute("authorRequest", author);
            return "admin/add-author";
        }
        authorService.addAuthor(new Author(author.getName()));
        return "redirect:/admin?success";
    }

    @GetMapping("/books/edit/{bookId}")
    public String showEditBookPage(@PathVariable Long bookId, Model model) {
        Book book = bookService.getBookById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        model.addAttribute("book", book);
        model.addAttribute("bookRequest", new EditBookRequest());
        model.addAttribute("authors", authorService.getAllAuthors());
        return "admin/edit-book";
    }

    @PostMapping("/books/edit/{bookId}")
    public String editBook(@PathVariable Long bookId, @ModelAttribute("bookRequest") @Valid EditBookRequest bookRequest, BindingResult result, Model model) {
        Book existingBook = bookService.getBookById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (bookRequest.getTitle() != null && !bookRequest.getTitle().isEmpty()) {
            existingBook.setTitle(bookRequest.getTitle());
        }
        if (bookRequest.getAuthorId() != null) {
            Author author = authorService.getAuthorById(bookRequest.getAuthorId())
                    .orElseThrow(() -> new RuntimeException("Author not found"));
            existingBook.setAuthor(author);
        }
        if (bookRequest.getGenre() != null && !bookRequest.getGenre().isEmpty()) {
            existingBook.setGenre(bookRequest.getGenre());
        }
        if (bookRequest.getDescription() != null && !bookRequest.getDescription().isEmpty()) {
            existingBook.setDescription(bookRequest.getDescription());
        }
        if (bookRequest.getPublisher() != null && !bookRequest.getPublisher().isEmpty()) {
            existingBook.setPublisher(bookRequest.getPublisher());
        }
        if (result.hasErrors()) {
            System.out.println(result.getAllErrors());
            Book book = bookService.getBookById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
            model.addAttribute("book", book);
            model.addAttribute("bookRequest", bookRequest);
            model.addAttribute("authors", authorService.getAllAuthors());
            return "admin/edit-book";
        }

        bookService.saveBook(existingBook);
        return "redirect:/admin?success";

    }

    @GetMapping("/authors/edit/{authorId}")
    public String showEditAuthorPage(@PathVariable Long authorId, Model model) {
        Author author = authorService.getAuthorById(authorId)
                .orElseThrow(() -> new RuntimeException("Author not found"));
        model.addAttribute("author", author);
        model.addAttribute("authorRequest", new AuthorRequest());
        return "admin/edit-author";
    }

    @PostMapping("/authors/edit/{authorId}")
    public String editAuthor(@PathVariable Long authorId, @ModelAttribute("authorRequest") @Valid AuthorRequest authorRequest, BindingResult result, Model model) {
        if (result.hasErrors()) {
            System.out.println(result.getAllErrors());
            model.addAttribute("authorRequest", authorRequest);
            Author author = authorService.getAuthorById(authorId)
                    .orElseThrow(() -> new RuntimeException("Author not found"));
            model.addAttribute("author", author);
            return "admin/edit-author";
        }
        authorService.updateAuthor(authorId, new Author(authorRequest.getName()));
        return "redirect:/admin?success";
    }

    @PostMapping("/authors/delete/{authorId}")
    public String deleteAuthor(@PathVariable Long authorId) {
        authorService.deleteAuthor(authorId);
        return "redirect:/admin?success";
    }

    @PostMapping("/users/delete/{userId}")
    public String deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return "redirect:/admin?success";
    }

    @PostMapping("/comments/delete/{commentId}")
    public String deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return "redirect:/admin?success";
    }

    @GetMapping("comments/edit/{commentId}")
    public String showEditCommentPage(@PathVariable Long commentId, Model model) {
        Comment comment = commentService.getCommentById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        model.addAttribute("comment", comment);
        model.addAttribute("commentRequest", new CommentRequest());
        return "admin/edit-comment";
    }
    @PostMapping("/comments/edit/{commentId}")
    public String editComment(@PathVariable Long commentId, @ModelAttribute("commentRequest") @Valid CommentRequest commentRequest, BindingResult result, Model model) {
        if (result.hasErrors()) {
            System.out.println(result.getAllErrors());
            model.addAttribute("commentRequest", commentRequest);
            Comment comment = commentService.getCommentById(commentId)
                    .orElseThrow(() -> new RuntimeException("Comment not found"));

            model.addAttribute("comment", comment);
            return "admin/edit-comment";
        }
        commentService.updateComment(commentId, commentRequest.getText());
        return "redirect:/admin?success";
    }
}
