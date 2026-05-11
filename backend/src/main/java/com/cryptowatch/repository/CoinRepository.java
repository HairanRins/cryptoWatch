package com.cryptowatch.repository;

import com.cryptowatch.entity.Coin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CoinRepository extends JpaRepository<Coin, Long> {
    Optional<Coin> findBySymbolIgnoreCase(String symbol);
    Optional<Coin> findByCoinId(String coinId);
}
