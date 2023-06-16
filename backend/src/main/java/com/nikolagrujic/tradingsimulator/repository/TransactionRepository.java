package com.nikolagrujic.tradingsimulator.repository;

import com.nikolagrujic.tradingsimulator.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {
    List<Transaction> findAllByDateTimeGreaterThanEqual(LocalDateTime dateTime);
    Page<Transaction> findAllByUser_Email(String email, Pageable pageable);
    List<Transaction> findAllByUser_Email(String email);

    @Query("SELECT t FROM Transaction t " +
            "WHERE t.user.email = :email AND t.dateTime BETWEEN :startDateTime AND :endDateTime " +
            "AND (lower(t.symbol) LIKE lower(concat('%', :search, '%')) OR " +
            "lower(t.name) LIKE lower(concat('%', :search, '%')))"
    )
    Page<Transaction> findAllByFilter(
        @Param("email") String email,
        @Param("startDateTime") LocalDateTime startDateTime,
        @Param("endDateTime") LocalDateTime endDateTime,
        @Param("search") String search,
        Pageable pageable
    );
}