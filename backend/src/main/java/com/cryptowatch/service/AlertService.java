package com.cryptowatch.service;

import com.cryptowatch.dto.AlertDto;
import com.cryptowatch.dto.CreateAlertRequest;
import com.cryptowatch.entity.Alert;
import com.cryptowatch.entity.Coin;
import com.cryptowatch.entity.Portfolio;
import com.cryptowatch.exception.ResourceNotFoundException;
import com.cryptowatch.repository.AlertRepository;
import com.cryptowatch.repository.CoinRepository;
import com.cryptowatch.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AlertService {
    private final AlertRepository alertRepository;
    private final PortfolioRepository portfolioRepository;
    private final CoinRepository coinRepository;
    private final CoinService coinService;

    public List<AlertDto> getAlertsByPortfolio(Long portfolioId) {
        return alertRepository.findByPortfolioIdAndActiveTrue(portfolioId).stream()
            .map(this::mapToDto).toList();
    }

    @Transactional
    public AlertDto createAlert(Long portfolioId, CreateAlertRequest request) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
            .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found: " + portfolioId));
        Coin coin = coinRepository.findById(request.coinId())
            .orElseThrow(() -> new ResourceNotFoundException("Coin not found: " + request.coinId()));
        Alert alert = Alert.builder().portfolio(portfolio).coin(coin)
            .targetPrice(request.targetPrice()).condition(request.condition()).active(true).build();
        return mapToDto(alertRepository.save(alert));
    }

    @Transactional
    public List<AlertDto> checkAndTriggerAlerts() {
        List<Alert> active = alertRepository.findAllActiveAndNotTriggered();
        return active.stream().filter(a -> {
            if (a.getCoin().getCurrentPrice() == null) return false;
            boolean trigger = a.shouldTrigger(a.getCoin().getCurrentPrice());
            if (trigger) { a.setTriggeredAt(LocalDateTime.now()); a.setActive(false); alertRepository.save(a);
                log.info("🚨 Alert triggered: {} {} target {} (current: {})", a.getCoin().getSymbol(), a.getCondition(), a.getTargetPrice(), a.getCoin().getCurrentPrice()); }
            return trigger;
        }).map(this::mapToDto).toList();
    }

    private AlertDto mapToDto(Alert alert) {
        return AlertDto.builder().id(alert.getId()).coin(coinService.mapToDto(alert.getCoin()))
            .portfolioId(alert.getPortfolio().getId()).targetPrice(alert.getTargetPrice())
            .condition(alert.getCondition()).active(alert.isActive())
            .triggeredAt(alert.getTriggeredAt()).createdAt(alert.getCreatedAt()).build();
    }
}
