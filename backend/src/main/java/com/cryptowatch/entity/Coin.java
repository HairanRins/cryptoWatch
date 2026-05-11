package com.cryptowatch.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coins")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Coin {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private String name;
    @Column(nullable = false, unique = true) private String symbol;
    @Column(name = "coin_id", unique = true) private String coinId;
    @Column(name = "image_url") private String imageUrl;
    @Column(name = "current_price", precision = 20, scale = 8) private BigDecimal currentPrice;
    @Column(name = "market_cap", precision = 30, scale = 2) private BigDecimal marketCap;
    @Column(name = "change_24h", precision = 10, scale = 4) private BigDecimal change24h;
    @Column(name = "volume_24h", precision = 30, scale = 2) private BigDecimal volume24h;
    @Column(name = "last_updated") private LocalDateTime lastUpdated;
    @Version private Long version;
}
