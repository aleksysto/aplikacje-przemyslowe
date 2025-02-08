package com.example.aplikacjeprzemyslowe.repository;

import com.example.aplikacjeprzemyslowe.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    @Query("SELECT a FROM Author a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Author> searchByName(@Param("name") String name);
    @Query("SELECT a FROM Author a " +
            "JOIN a.books b " +
            "JOIN b.ratings r " +
            "GROUP BY a " +
            "ORDER BY AVG(r.rating) DESC")
    List<Author> findTopRatedAuthors();
    @Query("SELECT AVG(r.rating) FROM BookRating r JOIN r.book b WHERE b.author.id = :authorId")
    Optional<Double> findAverageRatingByAuthorId(@Param("authorId") Long authorId);
}
