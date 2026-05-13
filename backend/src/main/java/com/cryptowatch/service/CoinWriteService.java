package com.cryptowatch.service;

import com.cryptowatch.dto.CoinDto;
import com.cryptowatch.entity.Coin;
import com.cryptowatch.repository.CoinRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoinWriteService {
    private final CoinRepository coinRepository;

    @Transactional
    public void saveOrUpdateCoin(CoinDto dto) {
        if (dto.coinId() == null || dto.coinId().isEmpty()) return;
        Coin coin = coinRepository.findByCoinId(dto.coinId())
            .orElseGet(() -> { Coin c = new Coin(); c.setCoinId(dto.coinId()); return c; });
        coin.setName(dto.name());
        coin.setSymbol(dto.symbol());
        coin.setImageUrl(dto.imageUrl());
        if (dto.currentPrice() != null) coin.setCurrentPrice(dto.currentPrice());
        if (dto.marketCap() != null) coin.setMarketCap(dto.marketCap());
        if (dto.change24h() != null) coin.setChange24h(dto.change24h());
        if (dto.volume24h() != null) coin.setVolume24h(dto.volume24h());
        coinRepository.save(coin);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW) 
    public void saveOrUpdateCoin(Coin coin) {
        coinRepository.save(coin);
    }
}
