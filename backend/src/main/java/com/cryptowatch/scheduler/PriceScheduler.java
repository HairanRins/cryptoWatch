package com.cryptowatch.scheduler;

import com.cryptowatch.service.AlertService;
import com.cryptowatch.service.CoinService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PriceScheduler {
    private final CoinService coinService;
    private final AlertService alertService;

    @Scheduled(fixedRateString = "${app.scheduler.price-refresh-interval:60000}")
    public void refreshPrices() {
        try { coinService.refreshPrices(); } catch (Exception e) { log.error("❌ Error refreshing prices: {}", e.getMessage()); }
    }

    @Scheduled(fixedRateString = "${app.scheduler.alert-check-interval:30000}")
    public void checkAlerts() {
        try {
            var triggered = alertService.checkAndTriggerAlerts();
            if (!triggered.isEmpty()) log.info("🚨 {} alert(s) triggered!", triggered.size());
        } catch (Exception e) { log.error("❌ Error checking alerts: {}", e.getMessage()); }
    }
}
