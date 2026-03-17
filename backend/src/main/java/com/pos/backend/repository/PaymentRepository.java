package com.pos.backend.repository;

import com.pos.backend.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findBySale_Id(Long saleId);

    List<Payment> findByPaymentType(String paymentType);

    List<Payment> findByCountAsCash(Boolean countAsCash);

    List<Payment> findByAuthorized(Boolean authorized);

    @Query("SELECT p FROM Payment p WHERE p.paymentDateTime >= :startDate AND p.paymentDateTime < :endDate")
    List<Payment> findByDateRange(@Param("startDate") LocalDateTime startDate,
                                  @Param("endDate") LocalDateTime endDate);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.sale.id = :saleId")
    BigDecimal getTotalPaymentsBySale(@Param("saleId") Long saleId);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.sale.session.id = :sessionId AND p.countAsCash = true")
    BigDecimal getTotalCashBySession(@Param("sessionId") Long sessionId);

    long countBySale_Id(Long saleId);
}
