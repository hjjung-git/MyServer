package com.example.my_server.service;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UpbitClient {

    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://api.upbit.com/v1")
            .defaultHeader("Accept", "application/json")
            .build();

    public Map<String, BigDecimal> getPrices(List<String> coins) {
        if (coins == null || coins.isEmpty()) return Map.of();

        String markets = coins.stream()
                .map(c -> "KRW-" + c.toUpperCase())
                .collect(Collectors.joining(","));

        List<Map<String, Object>> tickers = restClient.get()
                .uri("/ticker?markets=" + markets)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        Map<String, BigDecimal> prices = new HashMap<>();
        if (tickers != null) {
            for (var t : tickers) {
                String market = (String) t.get("market");
                Object price = t.get("trade_price");
                if (market != null && price instanceof Number) {
                    prices.put(market.replace("KRW-", ""), new BigDecimal(price.toString()));
                }
            }
        }
        return prices;
    }
}
