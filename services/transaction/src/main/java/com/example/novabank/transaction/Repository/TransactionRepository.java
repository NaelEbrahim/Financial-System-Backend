package com.example.novabank.transaction.Repository;

import com.example.novabank.transaction.Enum.Status;
import com.example.novabank.transaction.Model.TransactionModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionModel, Long> {

    Optional<TransactionModel> findByReferenceNumber(String referenceNumber);

    @Query("""
                SELECT t
                FROM transaction t
                WHERE t.senderId = :userId
                   OR t.receiverId = :userId
                ORDER BY t.id DESC
            """)
    Page<TransactionModel> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("""
            SELECT COALESCE(SUM(t.amount), 0)
            FROM transaction t
            WHERE t.senderId = :userId
              AND t.status = :status
              AND t.createdAt >= :start
              AND t.createdAt < :end
            """)
    BigDecimal sumOutcome(@Param("userId") Long userId,
                          @Param("status") Status status,
                          @Param("start") LocalDateTime start,
                          @Param("end") LocalDateTime end);

    @Query("""
            SELECT COALESCE(SUM(t.amount), 0)
            FROM transaction t
            WHERE t.receiverId = :userId
              AND t.status = :status
              AND t.createdAt >= :start
              AND t.createdAt < :end
            """)
    BigDecimal sumIncome(@Param("userId") Long userId,
                         @Param("status") Status status,
                         @Param("start") LocalDateTime start,
                         @Param("end") LocalDateTime end);

}
