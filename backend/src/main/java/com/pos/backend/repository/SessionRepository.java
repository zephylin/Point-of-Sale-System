package com.pos.backend.repository;

import com.pos.backend.domain.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    
    List<Session> findByCashierId(Long cashierId);
    
    List<Session> findByRegisterId(Long registerId);
    
    List<Session> findByStatus(String status);
    
    List<Session> findByCashierIdAndStatus(Long cashierId, String status);
    
    List<Session> findByRegisterIdAndStatus(Long registerId, String status);
    
    Optional<Session> findTopByCashierIdAndStatusOrderByStartDateTimeDesc(Long cashierId, String status);
    
    Optional<Session> findTopByRegisterIdAndStatusOrderByStartDateTimeDesc(Long registerId, String status);
    
    @Query("SELECT s FROM Session s WHERE s.startDateTime >= :startDate AND s.startDateTime < :endDate")
    List<Session> findSessionsByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT s FROM Session s WHERE s.cashierId = :cashierId AND s.startDateTime >= :startDate AND s.startDateTime < :endDate")
    List<Session> findSessionsByCashierAndDateRange(@Param("cashierId") Long cashierId, 
                                                      @Param("startDate") LocalDateTime startDate, 
                                                      @Param("endDate") LocalDateTime endDate);
    
    long countByCashierId(Long cashierId);
    
    long countByRegisterId(Long registerId);
    
    long countByStatus(String status);
}
