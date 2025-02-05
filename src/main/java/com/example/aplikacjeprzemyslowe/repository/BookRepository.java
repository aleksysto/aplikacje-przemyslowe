package com.example.aplikacjeprzemyslowe.repository;

import com.example.aplikacjeprzemyslowe.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT b FROM Book b WHERE LOWER(b.author.name) LIKE LOWER(CONCAT('%', :authorName, '%'))")
    List<Book> findByAuthorNameContains(@Param("authorName") String authorName);

    @Query("SELECT b FROM Book b WHERE b.borrowed = false AND LOWER(b.author.name) LIKE LOWER(CONCAT('%', :authorName, '%'))")
    List<Book> findAvailableByAuthorNameContains(@Param("authorName") String authorName);

    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Book> findByTitleContains(@Param("title") String title);

    @Query("SELECT b FROM Book b WHERE b.borrowed = false AND LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Book> findAvailableByTitleContains(@Param("title") String title);

    @Query("SELECT b FROM Book b WHERE LOWER(b.genre) LIKE LOWER(CONCAT('%', :genre, '%'))")
    List<Book> findByGenreContains(@Param("genre") String genre);

    @Query("SELECT b FROM Book b WHERE b.borrowed = false AND LOWER(b.genre) LIKE LOWER(CONCAT('%', :genre, '%'))")
    List<Book> findAvailableByGenreContains(@Param("genre") String genre);

    @Query("SELECT b " +
            "FROM Book b LEFT JOIN b.ratings r " +
            "GROUP BY b.id " +
            "ORDER BY COALESCE(AVG(r.rating), 0) DESC")
    List<Book> findTopByAverageRating();
}
