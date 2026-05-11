package com.cryptowatch.config;

import com.cryptowatch.entity.Coin;
import com.cryptowatch.entity.Portfolio;
import com.cryptowatch.repository.CoinRepository;
import com.cryptowatch.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import java.math.BigDecimal;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {
    private final CoinRepository coinRepository;
    private final PortfolioRepository portfolioRepository;

    @Bean
    @Profile("!prod")
    public CommandLineRunner initData() {
        return args -> {
            if (coinRepository.count() == 0) {
                log.info("🚀 Initializing demo data...");
                createCoin("bitcoin", "Bitcoin", "BTC", "https://assets.coingecko.com/coins/images/1/large/bitcoin.png", new BigDecimal("67500.00"));
                createCoin("ethereum", "Ethereum", "ETH", "https://assets.coingecko.com/coins/images/279/large/ethereum.png", new BigDecimal("3450.00"));
                createCoin("solana", "Solana", "SOL", "https://assets.coingecko.com/coins/images/4128/large/solana.png", new BigDecimal("178.50"));
                createCoin("cardano", "Cardano", "ADA", "https://assets.coingecko.com/coins/images/975/large/cardano.png", new BigDecimal("0.45"));
                createCoin("polkadot", "Polkadot", "DOT", "https://assets.coingecko.com/coins/images/12171/large/polkadot.png", new BigDecimal("7.20"));
                createCoin("chainlink", "Chainlink", "LINK", "https://assets.coingecko.com/coins/images/877/large/chainlink-new-logo.png", new BigDecimal("14.80"));
                createCoin("avalanche-2", "Avalanche", "AVAX", "https://assets.coingecko.com/coins/images/12559/large/Avalanche_Circle_RedWhite_Trans.png", new BigDecimal("35.40"));
                createCoin("polygon-pos", "Polygon", "MATIC", "https://assets.coingecko.com/coins/images/4713/large/matic-token-icon.png", new BigDecimal("0.72"));
                log.info("✅ {} coins initialized", coinRepository.count());
            }
            if (portfolioRepository.count() == 0) {
                portfolioRepository.save(Portfolio.builder().name("Mon Portfolio Demo").build());
                log.info("✅ Demo portfolio created");
            }
        };
    }

    private void createCoin(String coinId, String name, String symbol, String imageUrl, BigDecimal price) {
        coinRepository.save(Coin.builder().coinId(coinId).name(name).symbol(symbol)
            .imageUrl(imageUrl).currentPrice(price).marketCap(price.multiply(new BigDecimal("19000000")))
            .change24h(new BigDecimal("2.34")).build());
    }
}
