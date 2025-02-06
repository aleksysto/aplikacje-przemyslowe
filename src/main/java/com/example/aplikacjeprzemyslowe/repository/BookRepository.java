package com.example.aplikacjeprzemyslowe.repository;

import com.example.aplikacjeprzemyslowe.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT b FROM Book b JOIN b.author a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :authorName, '%'))")
    List<Book> findByAuthorNameContains(@Param("authorName") String authorName);

    @Query("SELECT b FROM Book b JOIN b.author a WHERE b.borrowed = false AND LOWER(a.name) LIKE LOWER(CONCAT('%', :authorName, '%'))")
    List<Book> findAvailableByAuthorNameContains(@Param("authorName") String authorName);

    @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Book> findByTitleContains(@Param("title") String title);

    @Query("SELECT b FROM Book b WHERE b.borrowed = false AND LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    List<Book> findAvailableByTitleContains(@Param("title") String title);

    @Query("SELECT b FROM Book b WHERE LOWER(b.genre) LIKE LOWER(CONCAT('%', :genre, '%'))")
    List<Book> findByGenreContains(@Param("genre") String genre);

    @Query("SELECT b FROM Book b WHERE b.borrowed = false AND LOWER(b.genre) LIKE LOWER(CONCAT('%', :genre, '%'))")
    List<Book> findAvailableByGenreContains(@Param("genre") String genre);

    @Query("SELECT b FROM Book b LEFT JOIN b.ratings r GROUP BY b ORDER BY COALESCE(AVG(r.rating), 0) DESC")
    List<Book> findTopByAverageRating();

    @Query("SELECT COUNT(r) FROM BookRating r WHERE r.book.id = :bookId")
    int countRatingsForBook(@Param("bookId") Long bookId);

    @Query("SELECT b FROM Book b LEFT JOIN FETCH b.ratings LEFT JOIN FETCH b.comments WHERE b.id = :bookId")
    Optional<Book> findBookWithDetails(@Param("bookId") Long bookId);

    @Query("SELECT DISTINCT b.genre FROM Book b ORDER BY b.genre")
    List<String> findAllGenres();

}
