package com.example.aplikacjeprzemyslowe.repository;

import com.example.aplikacjeprzemyslowe.entity.BookRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookRatingRepository extends JpaRepository<BookRating, Long> {

    @Query("SELECT br FROM BookRating br WHERE br.book.id = :bookId AND br.user.id = :userId")
    Optional<BookRating> findByBookAndUser(@Param("bookId") Long bookId, @Param("userId") Long userId);

    @Query("SELECT COALESCE(AVG(br.rating), 0) FROM BookRating br WHERE br.book.id = :bookId")
    Double findAverageRatingByBook(@Param("bookId") Long bookId);
}
