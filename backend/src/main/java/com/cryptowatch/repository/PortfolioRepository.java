package com.cryptowatch.repository;

import com.cryptowatch.entity.Portfolio;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    List<Portfolio> findByUserId(UUID userId);

    @EntityGraph(attributePaths = {"transactions", "transactions.coin", "alerts", "alerts.coin"})
    @Query("SELECT p FROM Portfolio p WHERE p.id = :id AND p.userId = :userId")
    Optional<Portfolio> findByIdAndUserId(@Param("id") Long id, @Param("userId") UUID userId);
}
