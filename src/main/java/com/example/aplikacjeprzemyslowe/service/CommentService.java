package com.example.aplikacjeprzemyslowe.service;

import com.example.aplikacjeprzemyslowe.entity.Book;
import com.example.aplikacjeprzemyslowe.entity.Comment;
import com.example.aplikacjeprzemyslowe.entity.CustomUserDetails;
import com.example.aplikacjeprzemyslowe.entity.User;
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

    public Comment addComment(Comment comment,Long bookId, CustomUserDetails user) {
        User persistentUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("User not found")); // ✅ Fetch the user from DB
        comment.setAuthor(persistentUser);
        Book persistentBook = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("User not found")); // ✅ Fetch the user from DB
        comment.setBook(persistentBook);
        return commentRepository.save(comment);
    }

    public Optional<Comment> getCommentById(Long commentId) {
        return commentRepository.findById(commentId);
    }

    public Comment updateComment(Comment comment) {
        return commentRepository.save(comment);
    }

    public void deleteComment(Comment comment) {
        commentRepository.delete(comment);
    }
}