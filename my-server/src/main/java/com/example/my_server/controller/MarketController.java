package com.example.my_server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/api")
public class MarketController {

    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://api.upbit.com/v1")
            .defaultHeader("Accept", "application/json")
            .build();

    private static final String TICKER_MARKETS =
            "KRW-BTC,KRW-ETH,KRW-SOL,KRW-XRP,KRW-ADA,KRW-DOGE,KRW-AVAX";

    @GetMapping("/ticker")
    public ResponseEntity<String> ticker() {
        String body = restClient.get()
                .uri("/ticker?markets=" + TICKER_MARKETS)
                .retrieve()
                .body(String.class);
        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(body);
    }

    @GetMapping("/candles/btc")
    public ResponseEntity<String> btcCandles() {
        String body = restClient.get()
                .uri("/candles/days?market=KRW-BTC&count=7")
                .retrieve()
                .body(String.class);
        return ResponseEntity.ok()
                .header("Content-Type", "application/json")
                .body(body);
    }
}
