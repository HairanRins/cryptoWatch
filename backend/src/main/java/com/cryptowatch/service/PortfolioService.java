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
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final TransactionRepository transactionRepository;
    private final CoinRepository coinRepository;
    private final CoinService coinService;

    public List<PortfolioDto> getPortfoliosByUser(UUID userId) {
        return portfolioRepository.findByUserId(userId).stream()
            .map(this::mapToSummaryDto)
            .toList();
    }

    public PortfolioDto getPortfolio(Long id, UUID userId) {
        Portfolio portfolio = portfolioRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found: " + id));
        return buildPortfolioDto(portfolio);
    }

    @Transactional
    public PortfolioDto createPortfolio(UUID userId, CreatePortfolioRequest request) {
        Portfolio portfolio = Portfolio.builder()
            .name(request.name())
            .userId(userId)
            .build();
        Portfolio saved = portfolioRepository.save(portfolio);
        return mapToSummaryDto(saved);
    }

    @Transactional
    public void deletePortfolio(Long id, UUID userId) {
        Portfolio portfolio = portfolioRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found: " + id));
        portfolioRepository.delete(portfolio);
    }

    @Transactional
    public TransactionDto addTransaction(Long portfolioId, UUID userId, CreateTransactionRequest request) {
        Portfolio portfolio = portfolioRepository.findByIdAndUserId(portfolioId, userId)
            .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found: " + portfolioId));
        
        Coin coin = coinRepository.findById(request.coinId())
            .orElseThrow(() -> new ResourceNotFoundException("Coin not found: " + request.coinId()));
        
        Transaction transaction = Transaction.builder()
            .portfolio(portfolio)
            .coin(coin)
            .type(request.type())
            .quantity(request.quantity())
            .price(request.price())
            .date(request.date())
            .build();
        
        return mapToTransactionDto(transactionRepository.save(transaction));
    }

    @Transactional
    public void deleteTransaction(Long transactionId) {
        if (!transactionRepository.existsById(transactionId)) {
            throw new ResourceNotFoundException("Transaction not found: " + transactionId);
        }
        transactionRepository.deleteById(transactionId);
    }

    private PortfolioDto buildPortfolioDto(Portfolio portfolio) {
        List<Transaction> transactions = portfolio.getTransactions();

        Map<Coin, List<Transaction>> txByCoin = transactions.stream()
            .collect(Collectors.groupingBy(Transaction::getCoin));

        List<PortfolioAssetDto> assets = new ArrayList<>();
        BigDecimal totalInvested = BigDecimal.ZERO;
        BigDecimal totalValue = BigDecimal.ZERO;

        for (Map.Entry<Coin, List<Transaction>> entry : txByCoin.entrySet()) {
            Coin coin = entry.getKey();
            List<Transaction> coinTxs = entry.getValue();

            BigDecimal quantity = BigDecimal.ZERO;
            BigDecimal invested = BigDecimal.ZERO;

            for (Transaction tx : coinTxs) {
                if (tx.getType() == TransactionType.BUY) {
                    quantity = quantity.add(tx.getQuantity());
                    invested = invested.add(tx.getTotalValue());
                } else {
                    quantity = quantity.subtract(tx.getQuantity());
                }
            }

            if (quantity.compareTo(BigDecimal.ZERO) <= 0) continue;

            BigDecimal avgBuyPrice = invested.divide(quantity, 8, RoundingMode.HALF_UP);
            BigDecimal currentPrice = coin.getCurrentPrice() != null ? coin.getCurrentPrice() : avgBuyPrice;
            BigDecimal currentValue = currentPrice.multiply(quantity);
            BigDecimal pnl = currentValue.subtract(invested);
            BigDecimal pnlPercent = invested.compareTo(BigDecimal.ZERO) > 0
                ? pnl.multiply(BigDecimal.valueOf(100)).divide(invested, 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

            totalInvested = totalInvested.add(invested);
            totalValue = totalValue.add(currentValue);

            assets.add(PortfolioAssetDto.builder()
                .coinId(coin.getId())
                .coinName(coin.getName())
                .coinSymbol(coin.getSymbol())
                .coinImageUrl(coin.getImageUrl())
                .quantity(quantity)
                .avgBuyPrice(avgBuyPrice)
                .currentPrice(currentPrice)
                .currentValue(currentValue)
                .invested(invested)
                .pnl(pnl)
                .pnlPercent(pnlPercent)
                .portfolioPercent(BigDecimal.ZERO)
                .build());
        }

        for (PortfolioAssetDto asset : assets) {
            BigDecimal finalTotalValue = totalValue.compareTo(BigDecimal.ZERO) > 0 ? totalValue : BigDecimal.ONE;
            BigDecimal percent = asset.currentValue()
                .multiply(BigDecimal.valueOf(100))
                .divide(finalTotalValue, 2, RoundingMode.HALF_UP);
            assets.set(assets.indexOf(asset), PortfolioAssetDto.builder()
                .coinId(asset.coinId())
                .coinName(asset.coinName())
                .coinSymbol(asset.coinSymbol())
                .coinImageUrl(asset.coinImageUrl())
                .quantity(asset.quantity())
                .avgBuyPrice(asset.avgBuyPrice())
                .currentPrice(asset.currentPrice())
                .currentValue(asset.currentValue())
                .invested(asset.invested())
                .pnl(asset.pnl())
                .pnlPercent(asset.pnlPercent())
                .portfolioPercent(percent)
                .build());
        }

        BigDecimal totalPnl = totalValue.subtract(totalInvested);
        BigDecimal totalPnlPercent = totalInvested.compareTo(BigDecimal.ZERO) > 0
            ? totalPnl.multiply(BigDecimal.valueOf(100)).divide(totalInvested, 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

        return PortfolioDto.builder()
            .id(portfolio.getId())
            .name(portfolio.getName())
            .createdAt(portfolio.getCreatedAt())
            .updatedAt(portfolio.getUpdatedAt())
            .totalValue(totalValue)
            .totalInvested(totalInvested)
            .totalPnL(totalPnl)
            .totalPnLPercent(totalPnlPercent)
            .assets(assets)
            .transactions(transactions.stream().map(this::mapToTransactionDto).toList())
            .build();
    }
    
    private PortfolioDto mapToSummaryDto(Portfolio portfolio) {
        return PortfolioDto.builder()
            .id(portfolio.getId())
            .name(portfolio.getName())
            .createdAt(portfolio.getCreatedAt())
            .updatedAt(portfolio.getUpdatedAt())
            .build();
    }

    private TransactionDto mapToTransactionDto(Transaction t) {
        return TransactionDto.builder()
            .id(t.getId())
            .portfolioId(t.getPortfolio().getId())
            .coin(coinService.mapToDto(t.getCoin()))
            .type(t.getType())
            .quantity(t.getQuantity())
            .price(t.getPrice())
            .totalValue(t.getTotalValue())
            .date(t.getDate())
            .createdAt(t.getCreatedAt())
            .build();
    }
}
