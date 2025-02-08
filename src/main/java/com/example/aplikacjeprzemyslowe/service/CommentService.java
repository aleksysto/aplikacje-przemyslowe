package com.example.aplikacjeprzemyslowe.service;

import com.example.aplikacjeprzemyslowe.entity.Book;
import com.example.aplikacjeprzemyslowe.entity.Comment;
import com.example.aplikacjeprzemyslowe.entity.CustomUserDetails;
import com.example.aplikacjeprzemyslowe.entity.User;
import com.example.aplikacjeprzemyslowe.payload.CommentRequest;
import com.example.aplikacjeprzemyslowe.repository.BookRepository;
import com.example.aplikacjeprzemyslowe.repository.CommentRepository;
import com.example.aplikacjeprzemyslowe.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, UserRepository userRepository, BookRepository bookRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    public List<Comment> getCommentsForBook(Long bookId) {
        return commentRepository.findByBookId(bookId);
    }

    public Comment addComment(CommentRequest commentRequest, Long bookId, CustomUserDetails user) {
        User persistentUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found")); // ✅ Fetch the user from DB
        Comment comment = new Comment();
        comment.setText(commentRequest.getText());
        comment.setAuthor(persistentUser);
        Book persistentBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("User not found")); // ✅ Fetch the user from DB
        comment.setBook(persistentBook);
        return commentRepository.save(comment);
    }

    public Optional<Comment> getCommentById(Long commentId) {
        return commentRepository.findById(commentId);
    }

    public Comment updateComment(Long commentId, String text) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setText(text);
        return commentRepository.save(comment);
    }

    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }
}