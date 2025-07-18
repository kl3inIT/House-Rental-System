package com.rental.houserental.repository;

import com.rental.houserental.dto.response.transaction.TransactionStatsDTO;
import com.rental.houserental.entity.Transaction;
import com.rental.houserental.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
        @Query("SELECT t FROM Transaction t WHERE " +
                        "(:type IS NULL OR t.type = :type) AND " +
                        "(:dateFrom IS NULL OR t.createdAt >= :dateFrom) AND " +
                        "(:dateTo IS NULL OR t.createdAt <= :dateTo) AND " +
                        "(:amountFrom IS NULL OR t.amount >= :amountFrom) AND " +
                        "(:amountTo IS NULL OR t.amount <= :amountTo) AND " +
                        "(:userId IS NULL OR t.user.id = :userId) " +
                        "ORDER BY t.createdAt DESC")
        Page<Transaction> findAllBySearchRequest(
                        @Param("type") TransactionType type,
                        @Param("dateFrom") LocalDateTime dateFrom,
                        @Param("dateTo") LocalDateTime dateTo,
                        @Param("amountFrom") Double amountFrom,
                        @Param("amountTo") Double amountTo,
                        @Param("userId") Long userId,
                        Pageable pageable);

        @Query("SELECT t FROM Transaction t " +
                        "JOIN t.user u " +
                        "WHERE (:type IS NULL OR t.type = :type) AND " +
                        "(:dateFrom IS NULL OR t.createdAt >= :dateFrom) AND " +
                        "(:dateTo IS NULL OR t.createdAt <= :dateTo) AND " +
                        "(:amountFrom IS NULL OR t.amount >= :amountFrom) AND " +
                        "(:amountTo IS NULL OR t.amount <= :amountTo) AND " +
                        "(:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) " +
                        "ORDER BY t.createdAt DESC")
        Page<Transaction> findAllBySearchRequestForAdmin(
                        @Param("type") TransactionType type,
                        @Param("dateFrom") LocalDateTime dateFrom,
                        @Param("dateTo") LocalDateTime dateTo,
                        @Param("amountFrom") Double amountFrom,
                        @Param("amountTo") Double amountTo,
                        @Param("email") String email,
                        Pageable pageable);

        @Query("SELECT new com.rental.houserental.dto.response.transaction.TransactionStatsDTO( " +
                        "COALESCE(SUM(t.amount), 0), " +
                        "COUNT(t), " +
                        "COUNT(DISTINCT t.user.id)) " +
                        "FROM Transaction t " +
                        "WHERE t.type = 'DEPOSIT'")
        TransactionStatsDTO getDepositStats();

        @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user.id = :userId AND t.type = 'RENT' AND t.createdAt >= :start AND t.createdAt <= :end")
        Long getLandlordRevenueByPeriod(@Param("userId") Long userId, @Param("start") LocalDateTime start,
                        @Param("end") LocalDateTime end);
}
