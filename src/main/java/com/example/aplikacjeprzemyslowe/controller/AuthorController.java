package com.example.aplikacjeprzemyslowe.controller;

import com.example.aplikacjeprzemyslowe.entity.Author;
import com.example.aplikacjeprzemyslowe.service.AuthorService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/authors")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    // ✅ Get all authors
    @GetMapping
    public List<Author> getAllAuthors() {
        return authorService.getAllAuthors();
    }

    // ✅ Get a specific author by ID
    @GetMapping("/{id}")
    public ResponseEntity<Author> getAuthorById(@PathVariable Long id) {
        Optional<Author> author = authorService.getAuthorById(id);
        return author.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    // ✅ Search authors by name (e.g., "John D" -> "John Doe", "John Deere", etc.)
    @GetMapping("/search")
    public List<Author> searchAuthors(@RequestParam String name) {
        return authorService.searchAuthorsByName(name);
    }


    // ✅ Add a new author
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Author> addAuthor(@RequestBody Author author) {
        Author savedAuthor = authorService.addAuthor(author);
        return ResponseEntity.ok(savedAuthor);
    }

    // ✅ Update an existing author
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Author> updateAuthor(@PathVariable Long id, @RequestBody Author updatedAuthor) {
        Author author = authorService.updateAuthor(id, updatedAuthor);
        return ResponseEntity.ok(author);
    }

    // ✅ Delete an author
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }
}
