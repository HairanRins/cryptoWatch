package com.cryptowatch.client;

import com.cryptowatch.dto.CoinDto;
import com.cryptowatch.dto.PricePointDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class CoinGeckoClient {
    private final RestTemplate restTemplate;
    @Value("${coingecko.api.base-url:https://api.coingecko.com/api/v3}") private String baseUrl;

    @Cacheable(value = "coins", key = "'top_' + #limit + '_' + #currency")
    public List<CoinDto> getTopCoins(int limit, String currency) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl).path("/coins/markets")
                .queryParam("vs_currency", currency).queryParam("order", "market_cap_desc")
                .queryParam("per_page", Math.min(limit, 100)).queryParam("page", 1)
                .queryParam("sparkline", false).queryParam("price_change_percentage", "24h").toUriString();
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
            List<Map<String, Object>> data = response.getBody();
            return data == null ? Collections.emptyList() : data.stream().map(this::mapToCoinDto).toList();
        } catch (RestClientException e) { return Collections.emptyList(); }
    }

    @Cacheable(value = "coin", key = "#coinId + '_' + #currency")
    public Optional<CoinDto> getCoinById(String coinId, String currency) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl).path("/coins/" + coinId)
                .queryParam("localization", false).queryParam("tickers", false)
                .queryParam("market_data", true).queryParam("community_data", false)
                .queryParam("developer_data", false).queryParam("sparkline", false).toUriString();
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
            Map<String, Object> data = response.getBody();
            return data == null ? Optional.empty() : Optional.of(mapDetailedCoin(data, currency));
        } catch (RestClientException e) { return Optional.empty(); }
    }

    @Cacheable(value = "prices", key = "#coinIds.hashCode() + '_' + #currency")
    public Map<String, BigDecimal> getPrices(List<String> coinIds, String currency) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl).path("/simple/price")
                .queryParam("ids", String.join(",", coinIds)).queryParam("vs_currencies", currency)
                .queryParam("include_24hr_change", true).toUriString();
            ResponseEntity<Map<String, Map<String, Object>>> response = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
            Map<String, Map<String, Object>> data = response.getBody();
            if (data == null) return Collections.emptyMap();
            Map<String, BigDecimal> prices = new HashMap<>();
            data.forEach((id, priceData) -> { Object price = priceData.get(currency); if (price != null) prices.put(id, new BigDecimal(price.toString())); });
            return prices;
        } catch (RestClientException e) { return Collections.emptyMap(); }
    }

    @Cacheable(value = "history", key = "#coinId + '_' + #days + '_' + #currency")
    public List<PricePointDto> getPriceHistory(String coinId, int days, String currency) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl).path("/coins/" + coinId + "/market_chart")
                .queryParam("vs_currency", currency).queryParam("days", days).toUriString();
            ResponseEntity<Map<String, List<List<Object>>>> response = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
            Map<String, List<List<Object>>> data = response.getBody();
            if (data == null || !data.containsKey("prices")) return Collections.emptyList();
            return data.get("prices").stream().map(point -> {
                long timestamp = ((Number) point.get(0)).longValue();
                double price = ((Number) point.get(1)).doubleValue();
                return PricePointDto.builder().timestamp(LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault())).price(BigDecimal.valueOf(price)).build();
            }).toList();
        } catch (RestClientException e) { return Collections.emptyList(); }
    }

    private CoinDto mapToCoinDto(Map<String, Object> data) {
        return CoinDto.builder().coinId(getString(data, "id")).name(getString(data, "name"))
            .symbol(getString(data, "symbol").toUpperCase()).imageUrl(getString(data, "image"))
            .currentPrice(getBigDecimal(data, "current_price")).marketCap(getBigDecimal(data, "market_cap"))
            .change24h(getBigDecimal(data, "price_change_percentage_24h")).volume24h(getBigDecimal(data, "total_volume"))
            .lastUpdated(LocalDateTime.now()).build();
    }

    private CoinDto mapDetailedCoin(Map<String, Object> data, String currency) {
        Map<String, Object> marketData = (Map<String, Object>) data.get("market_data");
        Map<String, Object> currentPrice = marketData != null ? (Map<String, Object>) marketData.get("current_price") : null;
        return CoinDto.builder().coinId(getString(data, "id")).name(getString(data, "name"))
            .symbol(getString(data, "symbol").toUpperCase()).imageUrl(getString(data, "image"))
            .currentPrice(currentPrice != null ? getBigDecimal(currentPrice, currency) : null)
            .marketCap(marketData != null ? getBigDecimal(marketData, "market_cap") : null)
            .change24h(marketData != null ? getBigDecimal(marketData, "price_change_percentage_24h") : null)
            .volume24h(marketData != null ? getBigDecimal(marketData, "total_volume") : null)
            .lastUpdated(LocalDateTime.now()).build();
    }

    private String getString(Map<String, Object> map, String key) { Object v = map.get(key); return v != null ? v.toString() : ""; }
    private BigDecimal getBigDecimal(Map<String, Object> map, String key) {
        Object v = map.get(key); if (v == null) return null; if (v instanceof BigDecimal bd) return bd; if (v instanceof Number n) return BigDecimal.valueOf(n.doubleValue());
        try { return new BigDecimal(v.toString()); } catch (NumberFormatException e) { return null; }
    }
}
