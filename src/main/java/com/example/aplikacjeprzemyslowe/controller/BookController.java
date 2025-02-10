package com.example.aplikacjeprzemyslowe.controller;

import com.example.aplikacjeprzemyslowe.entity.*;
import com.example.aplikacjeprzemyslowe.payload.CommentRequest;
import com.example.aplikacjeprzemyslowe.payload.RatingRequest;
import com.example.aplikacjeprzemyslowe.service.AuthorService;
import com.example.aplikacjeprzemyslowe.service.BookService;
import com.example.aplikacjeprzemyslowe.service.CommentService;
import com.example.aplikacjeprzemyslowe.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/")
public class BookController {

    private final BookService bookService;
    private final AuthorService authorService;
    private final CommentService commentService;
    private final UserService userService;

    public BookController(BookService bookService, AuthorService authorService, CommentService commentService, UserService userService) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.commentService = commentService;
        this.userService = userService;
    }
    @GetMapping
    public String mainPage(Model model) {
        // Fetch the top-rated books
        List<Book> topRatedBooks = bookService.getTop5ByAverageRating(false);
        model.addAttribute("topRatedBooks", topRatedBooks);
        Map<Long, Double> bookRatings = new HashMap<>();
        for (Book book : topRatedBooks) {
            Double avgRating = bookService.getAverageRating(book.getId());
            bookRatings.put(book.getId(), avgRating);
        }
        Map<Author, Double> topRatedAuthors = authorService.getTopRatedAuthorsWithRatings();



        model.addAttribute("topRatedBooks", topRatedBooks);
        model.addAttribute("topRatedAuthors", topRatedAuthors);
        model.addAttribute("bookRatings", bookRatings);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated();
        boolean isAdmin = isAuthenticated && authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("isAdmin", isAdmin);

        return "home";
    }
    @GetMapping("/search")
    public String searchBooks(@RequestParam("query") String query,
                              @RequestParam("type") String type,
                              Model model) {
        List<Book> searchResults;

        switch (type) {
            case "title":
                searchResults = bookService.searchBooksByTitle(query, false);
                break;
            case "author":
                searchResults = bookService.searchBooksByAuthor(query, false);
                break;
            case "genre":
                searchResults = bookService.searchBooksByGenre(query, false);
                break;
            default:
                searchResults = List.of(); // Empty list if type is unknown
        }

        model.addAttribute("searchResults", searchResults);
        model.addAttribute("query", query);
        model.addAttribute("type", type);
        Map<Long, Double> bookRatings = new HashMap<>();
        for (Book book : searchResults) {
            Double avgRating = bookService.getAverageRating(book.getId());
            bookRatings.put(book.getId(), avgRating);
        }
        model.addAttribute("bookRatings", bookRatings);

        return "books/search-results";
    }
    @GetMapping("books/{bookId}")
    public String getBookDetails(@PathVariable Long bookId, Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Book book = bookService.getBookById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        List<Comment> comments = commentService.getCommentsForBook(bookId);

        model.addAttribute("book", book);
        model.addAttribute("comments", comments);
        model.addAttribute("commentRequest", new CommentRequest());
        model.addAttribute("ratingRequest", new RatingRequest());
        Optional<BookRating> yourRating = bookService.findRatingByBookAndUser(bookId, userDetails.getId());
        if (yourRating.isPresent()) {
            model.addAttribute("yourRating", yourRating.get().getRating());
        } else {
            model.addAttribute("yourRating", null);
        }
        model.addAttribute("avgRating", bookService.getAverageRating(bookId));
        return "books/book-details";
    }
    @PostMapping("books/{bookId}/rate")
    public String rateBook(@PathVariable Long bookId, @Valid @ModelAttribute("ratingRequest") RatingRequest ratingRequest,
                           BindingResult bindingResult, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (bindingResult.hasErrors()) {
            Book book = bookService.getBookById(bookId)
                    .orElseThrow(() -> new RuntimeException("Book not found"));
            List<Comment> comments = commentService.getCommentsForBook(bookId);

            model.addAttribute("book", book);
            model.addAttribute("comments", comments);
            model.addAttribute("commentRequest", new CommentRequest());
            model.addAttribute("ratingRequest", new RatingRequest());
            model.addAttribute("yourRating", bookService.findRatingByBookAndUser(bookId, userDetails.getId()));
            model.addAttribute("avgRating", bookService.getAverageRating(bookId));
            return "books/book-details";
        }
        User user = userService.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        bookService.rateBook(bookId, user.getId(), ratingRequest.getRating());
        return "redirect:/books/" + bookId + "?success";
    }

    @PostMapping("books/{bookId}/comment")
    public String addComment(@PathVariable Long bookId, @Valid @ModelAttribute("commentRequest") CommentRequest commentRequest,
                             BindingResult bindingResult, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

        if (commentRequest.getText() == null || commentRequest.getText().trim().isEmpty()) {
            bindingResult.rejectValue("text", "error.commentRequest", "Comment text is required");
        }
        if (bindingResult.hasErrors()) {
            Book book = bookService.getBookById(bookId)
                    .orElseThrow(() -> new RuntimeException("Book not found"));
            List<Comment> comments = commentService.getCommentsForBook(bookId);

            model.addAttribute("book", book);
            model.addAttribute("comments", comments);
            model.addAttribute("commentRequest", commentRequest);
            model.addAttribute("ratingRequest", new RatingRequest());
            model.addAttribute("yourRating", bookService.findRatingByBookAndUser(bookId, userDetails.getId()));
            model.addAttribute("avgRating", bookService.getAverageRating(bookId));
            return "books/book-details";
        }
        commentService.addComment(commentRequest, bookId, userDetails);
        return "redirect:/books/" + bookId + "?success";
    }
    @PostMapping("books/{bookId}/borrow")
    public String borrowBook(@PathVariable Long bookId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        bookService.borrowBook(bookId, userDetails);
        return "redirect:/books/" + bookId + "?success";

    }
    @PostMapping("books/{bookId}/return")
    public String returnBook(@PathVariable Long bookId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        bookService.returnBook(bookId, userDetails);
        return "redirect:/books/" + bookId + "?success";
    }
    @GetMapping("books/{bookId}/edit/{commentId}")
    public String showEditCommentPage(@PathVariable Long commentId, @PathVariable Long bookId, Model model) {
        Book book = bookService.getBookById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        Comment comment = commentService.getCommentById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        model.addAttribute("comment", comment);
        model.addAttribute("commentRequest", new CommentRequest());
        model.addAttribute("book", book);
        return "comments/edit-comment";
    }
    @PostMapping("books/{bookId}/edit/{commentId}")
    public String editComment(@PathVariable Long commentId,
                              @PathVariable Long bookId,
                              @Valid @ModelAttribute("commentRequest") CommentRequest commentRequest,
    BindingResult result,
    Model model,
    @AuthenticationPrincipal CustomUserDetails userDetails) {
        Book book = bookService.getBookById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        Comment comment = commentService.getCommentById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        if(comment.getAuthor().getId() != userDetails.getId()) {
            result.rejectValue("text", "error.commentUpdateRequest", "Cannot edit someone else's comment");
            model.addAttribute("commentRequest", commentRequest);
            model.addAttribute("book", book);
            model.addAttribute("comment", comment);
            return "comments/edit-comment";
        }

        if (result.hasErrors()) {
            System.out.println(result.getAllErrors());
            model.addAttribute("commentRequest", commentRequest);

            model.addAttribute("comment", comment);
            return "comments/edit-comment";
        }
        commentService.updateComment(commentId, commentRequest.getText());
        return "redirect:/books/" + bookId + "?success";
    }
}
