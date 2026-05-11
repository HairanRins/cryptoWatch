package com.cryptowatch.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "alerts")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Alert {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "coin_id", nullable = false)
    private Coin coin;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;
    @Column(name = "target_price", nullable = false, precision = 20, scale = 8) private BigDecimal targetPrice;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private AlertCondition condition;
    @Column(nullable = false) @Builder.Default private boolean active = true;
    @Column(name = "triggered_at") private LocalDateTime triggeredAt;
    @Column(name = "created_at") private LocalDateTime createdAt;
    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); }
    
    public boolean shouldTrigger(BigDecimal currentPrice) {
        if (!active || triggeredAt != null) return false;
        return switch (condition) {
            case ABOVE -> currentPrice.compareTo(targetPrice) >= 0;
            case BELOW -> currentPrice.compareTo(targetPrice) <= 0;
        };
    }
}
