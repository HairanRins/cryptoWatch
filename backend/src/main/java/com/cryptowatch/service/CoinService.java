package com.cryptowatch.service;

import com.cryptowatch.client.CoinGeckoClient;
import com.cryptowatch.dto.CoinDto;
import com.cryptowatch.entity.Coin;
import com.cryptowatch.repository.CoinRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CoinService {
    private final CoinRepository coinRepository;
    private final CoinGeckoClient coinGeckoClient;
    private final CoinWriteService coinWriteService;
    private static final String DEFAULT_CURRENCY = "usd";

    public List<CoinDto> getTopCoins() {
        List<CoinDto> coins = coinGeckoClient.getTopCoins(100, DEFAULT_CURRENCY);
        coins.forEach(coinWriteService::saveOrUpdateCoin);
        return coins;
    }

    public Optional<CoinDto> getCoinById(String coinId) {
        return coinGeckoClient.getCoinById(coinId, DEFAULT_CURRENCY)
            .map(dto -> { coinWriteService.saveOrUpdateCoin(dto); return dto; });
    }

    @Transactional
    public void refreshPrices() {
        List<Coin> coins = coinRepository.findAll();
        if (coins.isEmpty()) return;
        List<String> coinIds = coins.stream().map(Coin::getCoinId).filter(id -> id != null && !id.isEmpty()).toList();
        if (coinIds.isEmpty()) return;
        var prices = coinGeckoClient.getPrices(coinIds, DEFAULT_CURRENCY);
        coins.stream().filter(c -> prices.containsKey(c.getCoinId()))
            .forEach(coin -> coin.setCurrentPrice(prices.get(coin.getCoinId())));
        coinRepository.saveAll(coins);
    }

    CoinDto mapToDto(Coin coin) {
        return CoinDto.builder().id(coin.getId()).name(coin.getName()).symbol(coin.getSymbol())
            .coinId(coin.getCoinId()).imageUrl(coin.getImageUrl()).currentPrice(coin.getCurrentPrice())
            .marketCap(coin.getMarketCap()).change24h(coin.getChange24h())
            .volume24h(coin.getVolume24h()).lastUpdated(coin.getLastUpdated()).build();
    }
}
