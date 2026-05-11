package com.cryptowatch.service;

import com.cryptowatch.dto.*;
import com.cryptowatch.entity.*;
import com.cryptowatch.exception.ResourceNotFoundException;
import com.cryptowatch.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PortfolioService {
    private final PortfolioRepository portfolioRepository;
    private final TransactionRepository transactionRepository;
    private final CoinRepository coinRepository;
    private final CoinService coinService;

    public PortfolioDto getPortfolio(Long id) {
        Portfolio portfolio = portfolioRepository.findByIdWithDetails(id)
            .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found: " + id));
        return buildPortfolioDto(portfolio);
    }

    @Transactional
    public TransactionDto addTransaction(Long portfolioId, CreateTransactionRequest request) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
            .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found: " + portfolioId));
        Coin coin = coinRepository.findById(request.coinId())
            .orElseThrow(() -> new ResourceNotFoundException("Coin not found: " + request.coinId()));
        Transaction transaction = Transaction.builder().portfolio(portfolio).coin(coin)
            .type(request.type()).quantity(request.quantity()).price(request.price())
            .date(request.date()).build();
        return mapToTransactionDto(transactionRepository.save(transaction));
    }

    private PortfolioDto buildPortfolioDto(Portfolio portfolio) {
        List<Transaction> transactions = transactionRepository.findByPortfolioIdOrderByDateDesc(portfolio.getId());
        Map<Long, List<Transaction>> byCoin = new HashMap<>();
        for (Transaction t : transactions) byCoin.computeIfAbsent(t.getCoin().getId(), k -> new ArrayList<>()).add(t);
        
        List<PortfolioAssetDto> assets = new ArrayList<>();
        BigDecimal totalValue = BigDecimal.ZERO, totalInvested = BigDecimal.ZERO;
        for (var entry : byCoin.entrySet()) {
            Coin coin = entry.getValue().get(0).getCoin();
            BigDecimal qty = BigDecimal.ZERO, inv = BigDecimal.ZERO;
            for (Transaction t : entry.getValue()) {
                if (t.getType() == TransactionType.BUY) { qty = qty.add(t.getQuantity()); inv = inv.add(t.getTotalValue()); }
                else qty = qty.subtract(t.getQuantity());
            }
            BigDecimal avg = qty.compareTo(BigDecimal.ZERO) > 0 ? inv.divide(qty, 8, RoundingMode.HALF_UP) : BigDecimal.ZERO;
            BigDecimal price = coin.getCurrentPrice() != null ? coin.getCurrentPrice() : BigDecimal.ZERO;
            BigDecimal val = qty.multiply(price), pnl = val.subtract(inv);
            BigDecimal pnlPct = inv.compareTo(BigDecimal.ZERO) > 0 ? pnl.divide(inv, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)) : BigDecimal.ZERO;
            totalValue = totalValue.add(val); totalInvested = totalInvested.add(inv);
            assets.add(PortfolioAssetDto.builder().coinId(coin.getId()).coinName(coin.getName())
                .coinSymbol(coin.getSymbol()).coinImageUrl(coin.getImageUrl()).quantity(qty)
                .avgBuyPrice(avg).currentPrice(price).currentValue(val).invested(inv)
                .pnl(pnl).pnlPercent(pnlPct).build());
        }
        
        List<PortfolioAssetDto> result = new ArrayList<>();
        for (PortfolioAssetDto a : assets) {
            BigDecimal pct = totalValue.compareTo(BigDecimal.ZERO) > 0 ? a.currentValue().divide(totalValue, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)) : BigDecimal.ZERO;
            result.add(PortfolioAssetDto.builder().coinId(a.coinId()).coinName(a.coinName())
                .coinSymbol(a.coinSymbol()).coinImageUrl(a.coinImageUrl()).quantity(a.quantity())
                .avgBuyPrice(a.avgBuyPrice()).currentPrice(a.currentPrice()).currentValue(a.currentValue())
                .invested(a.invested()).pnl(a.pnl()).pnlPercent(a.pnlPercent()).portfolioPercent(pct).build());
        }
        result.sort(Comparator.comparing(PortfolioAssetDto::currentValue).reversed());
        
        BigDecimal totalPnL = totalValue.subtract(totalInvested);
        BigDecimal totalPnLPct = totalInvested.compareTo(BigDecimal.ZERO) > 0 ? totalPnL.divide(totalInvested, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)) : BigDecimal.ZERO;
        return PortfolioDto.builder().id(portfolio.getId()).name(portfolio.getName())
            .totalValue(totalValue).totalInvested(totalInvested).totalPnL(totalPnL)
            .totalPnLPercent(totalPnLPct).assets(result).build();
    }

    private TransactionDto mapToTransactionDto(Transaction t) {
        return TransactionDto.builder().id(t.getId()).portfolioId(t.getPortfolio().getId())
            .coin(coinService.mapToDto(t.getCoin())).type(t.getType()).quantity(t.getQuantity())
            .price(t.getPrice()).totalValue(t.getTotalValue()).date(t.getDate())
            .createdAt(t.getCreatedAt()).build();
    }
}
