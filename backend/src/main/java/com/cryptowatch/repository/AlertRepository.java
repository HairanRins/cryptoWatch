package com.cryptowatch.repository;

import com.cryptowatch.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByPortfolioIdAndActiveTrue(Long portfolioId);
    @Query("SELECT a FROM Alert a JOIN FETCH a.coin WHERE a.active = true AND a.triggeredAt IS NULL")
    List<Alert> findAllActiveAndNotTriggered();
}
