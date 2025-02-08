package com.example.aplikacjeprzemyslowe.service;

import com.example.aplikacjeprzemyslowe.entity.Author;
import com.example.aplikacjeprzemyslowe.repository.AuthorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    @Autowired
    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }
    public List<Author> searchAuthorsByName(String name) {
        return authorRepository.searchByName(name);
    }
    public Map<Author, Double> getTopRatedAuthorsWithRatings() {
        List<Author> topAuthors = authorRepository.findTopRatedAuthors();
        return topAuthors.stream()
                .collect(Collectors.toMap(
                        author -> author,
                        author -> findAverageRatingByAuthorId(author.getId())
                ));
    }
    public Double findAverageRatingByAuthorId(Long authorId) {
        return authorRepository.findAverageRatingByAuthorId(authorId).orElse(0.0);
    }

    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    public Optional<Author> getAuthorById(Long id) {
        return authorRepository.findById(id);
    }

    public Author addAuthor(Author author) {
        return authorRepository.save(author);
    }

    public Author updateAuthor(Long id, Author updatedAuthor) {
        return authorRepository.findById(id)
                .map(existingAuthor -> {
                    existingAuthor.setName(updatedAuthor.getName());
                    return authorRepository.save(existingAuthor);
                })
                .orElseThrow(() -> new RuntimeException("Author not found!"));
    }

    public void deleteAuthor(Long id) {
        if (!authorRepository.existsById(id)) {
            throw new RuntimeException("Author not found!");
        }
        authorRepository.deleteById(id);
    }
    public List<Author> getTopRatedAuthors() {
        return authorRepository.findTopRatedAuthors();
    }
}
