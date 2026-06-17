package com.example.my_server.controller;

import com.example.my_server.domain.Holding;
import com.example.my_server.domain.User;
import com.example.my_server.repository.HoldingRepository;
import com.example.my_server.repository.UserRepository;
import com.example.my_server.service.UpbitClient;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Controller
public class PortfolioController {

    private final HoldingRepository holdingRepository;
    private final UserRepository userRepository;
    private final UpbitClient upbitClient;

    public PortfolioController(HoldingRepository holdingRepository,
                               UserRepository userRepository,
                               UpbitClient upbitClient) {
        this.holdingRepository = holdingRepository;
        this.userRepository = userRepository;
        this.upbitClient = upbitClient;
    }

    @GetMapping("/panel/portfolio")
    public String panelPortfolio(Model model) {
        String loginId = currentLoginId();
        if (loginId == null) {
            model.addAttribute("notLoggedIn", true);
            return "fragments/panel-portfolio :: portfolio";
        }

        List<Holding> holdings = holdingRepository.findByUserLoginId(loginId);
        model.addAttribute("holdings", holdings);

        if (!holdings.isEmpty()) {
            List<String> coins = holdings.stream().map(Holding::getCoin).toList();
            Map<String, BigDecimal> prices = upbitClient.getPrices(coins);

            BigDecimal totalInvest = BigDecimal.ZERO;
            BigDecimal totalEval   = BigDecimal.ZERO;
            List<Map<String, Object>> rows = new ArrayList<>();

            for (Holding h : holdings) {
                BigDecimal cur    = prices.getOrDefault(h.getCoin(), BigDecimal.ZERO);
                BigDecimal invest = h.getAvgPrice().multiply(h.getQuantity()).setScale(0, RoundingMode.HALF_UP);
                BigDecimal eval   = cur.multiply(h.getQuantity()).setScale(0, RoundingMode.HALF_UP);
                BigDecimal profit = eval.subtract(invest);
                BigDecimal rate   = h.getAvgPrice().signum() > 0
                        ? cur.subtract(h.getAvgPrice())
                              .divide(h.getAvgPrice(), 4, RoundingMode.HALF_UP)
                              .multiply(BigDecimal.valueOf(100))
                              .setScale(2, RoundingMode.HALF_UP)
                        : BigDecimal.ZERO;

                totalInvest = totalInvest.add(invest);
                totalEval   = totalEval.add(eval);

                Map<String, Object> row = new LinkedHashMap<>();
                row.put("holding",      h);
                row.put("currentPrice", cur);
                row.put("invest",       invest);
                row.put("eval",         eval);
                row.put("profit",       profit);
                row.put("profitRate",   rate);
                rows.add(row);
            }

            BigDecimal totalProfit     = totalEval.subtract(totalInvest);
            BigDecimal totalProfitRate = totalInvest.signum() > 0
                    ? totalProfit.divide(totalInvest, 4, RoundingMode.HALF_UP)
                                 .multiply(BigDecimal.valueOf(100))
                                 .setScale(2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            model.addAttribute("rows",            rows);
            model.addAttribute("totalInvest",     totalInvest);
            model.addAttribute("totalEval",       totalEval);
            model.addAttribute("totalProfit",     totalProfit);
            model.addAttribute("totalProfitRate", totalProfitRate);
        }

        return "fragments/panel-portfolio :: portfolio";
    }

    @PostMapping("/portfolio/holding")
    @ResponseBody
    public ResponseEntity<Void> upsert(@RequestParam String coin,
                                       @RequestParam BigDecimal avgPrice,
                                       @RequestParam BigDecimal quantity) {
        String loginId = currentLoginId();
        if (loginId == null) return ResponseEntity.status(401).build();

        User user = userRepository.findByLoginId(loginId).orElseThrow();
        String upperCoin = coin.toUpperCase().strip();

        Holding holding = holdingRepository
                .findByUserLoginIdAndCoin(loginId, upperCoin)
                .orElse(new Holding());
        holding.setUser(user);
        holding.setCoin(upperCoin);
        holding.setAvgPrice(avgPrice);
        holding.setQuantity(quantity);
        holdingRepository.save(holding);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/portfolio/holding/{id}/delete")
    @ResponseBody
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        String loginId = currentLoginId();
        if (loginId == null) return ResponseEntity.status(401).build();
        holdingRepository.findByIdAndUserLoginId(id, loginId)
                .ifPresent(holdingRepository::delete);
        return ResponseEntity.ok().build();
    }

    private String currentLoginId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails ud) return ud.getUsername();
        if (principal instanceof String s && !"anonymousUser".equals(s)) return s;
        return null;
    }
}
