package com.cryptowatch.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Transaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "coin_id", nullable = false)
    private Coin coin;
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private TransactionType type;
    @Column(nullable = false, precision = 20, scale = 8) private BigDecimal quantity;
    @Column(nullable = false, precision = 20, scale = 8) private BigDecimal price;
    @Column(nullable = false) private LocalDate date;
    @Column(name = "created_at") private LocalDateTime createdAt;
    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); }
    public BigDecimal getTotalValue() { return quantity.multiply(price); }
}
