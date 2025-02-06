package com.example.aplikacjeprzemyslowe.controller;

import com.example.aplikacjeprzemyslowe.entity.Comment;
import com.example.aplikacjeprzemyslowe.entity.CustomUserDetails;
import com.example.aplikacjeprzemyslowe.entity.User;
import com.example.aplikacjeprzemyslowe.payload.UpdateCommentRequest;
import com.example.aplikacjeprzemyslowe.service.CommentService;
import com.example.aplikacjeprzemyslowe.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
    public ResponseEntity<Comment> addComment(@PathVariable Long bookId, @RequestBody Comment comment, @AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(commentService.addComment(comment, bookId, user));
    }


    @PutMapping("/{commentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Comment> updateComment(
            @PathVariable Long commentId,
            @RequestBody UpdateCommentRequest request,  // ✅ Use DTO
            @AuthenticationPrincipal CustomUserDetails user) {

        Optional<Comment> optionalComment = commentService.getCommentById(commentId);

        if (optionalComment.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Comment comment = optionalComment.get();

        // ✅ Check if user is the owner or an admin
        if (!user.getId().equals(comment.getAuthor().getId()) &&
                !user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // ⛔ Access Denied
        }

        // ✅ Update comment text properly
        comment.setText(request.getText());
        return ResponseEntity.ok(commentService.updateComment(comment));
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("isAuthenticated()") // Only authenticated users can delete
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomUserDetails user) {

        Optional<Comment> optionalComment = commentService.getCommentById(commentId);

        if (optionalComment.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Comment comment = optionalComment.get();

        if (!user.getId().equals(comment.getAuthor().getId()) && !user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // ⛔ Access Denied
        }

        commentService.deleteComment(comment);
        return ResponseEntity.noContent().build();
    }

}