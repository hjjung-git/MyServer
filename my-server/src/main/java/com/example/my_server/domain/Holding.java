package com.example.my_server.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class Holding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 20)
    private String coin;

    @Column(nullable = false, precision = 20, scale = 8)
    private BigDecimal avgPrice;

    @Column(nullable = false, precision = 20, scale = 8)
    private BigDecimal quantity;

    public Long getId() { return id; }
    public User getUser() { return user; }
    public String getCoin() { return coin; }
    public BigDecimal getAvgPrice() { return avgPrice; }
    public BigDecimal getQuantity() { return quantity; }

    public void setUser(User user) { this.user = user; }
    public void setCoin(String coin) { this.coin = coin; }
    public void setAvgPrice(BigDecimal avgPrice) { this.avgPrice = avgPrice; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
}
