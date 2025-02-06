package com.example.aplikacjeprzemyslowe.controller;

import com.example.aplikacjeprzemyslowe.entity.Comment;
import com.example.aplikacjeprzemyslowe.entity.User;
import com.example.aplikacjeprzemyslowe.service.CommentService;
import com.example.aplikacjeprzemyslowe.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;

    public CommentController(CommentService commentService, UserService userService) {
        this.commentService = commentService;
        this.userService = userService;
    }

    @GetMapping("/book/{bookId}")
    public List<Comment> getComments(@PathVariable Long bookId) {
        return commentService.getCommentsForBook(bookId);
    }

    @PostMapping("/book/{bookId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Comment> addComment(@PathVariable Long bookId, @RequestBody Comment comment, @AuthenticationPrincipal User user) {
        comment.setAuthor(user);
        return ResponseEntity.ok(commentService.addComment(comment));
    }

    @PutMapping("/{commentId}")
    @PreAuthorize("#user.id == comment.author.id or hasRole('ADMIN')")
    public ResponseEntity<Comment> updateComment(@PathVariable Long commentId, @RequestBody String newText, @AuthenticationPrincipal User user) {
        Optional<Comment> optionalComment = commentService.getCommentById(commentId);
        if (optionalComment.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Comment comment = optionalComment.get();
        comment.setText(newText);
        return ResponseEntity.ok(commentService.updateComment(comment));
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("#user.id == comment.author.id or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId, @AuthenticationPrincipal User user) {
        Optional<Comment> optionalComment = commentService.getCommentById(commentId);
        if (optionalComment.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        commentService.deleteComment(optionalComment.get());
        return ResponseEntity.noContent().build();
    }
}