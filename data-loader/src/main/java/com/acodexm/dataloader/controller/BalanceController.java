package com.acodexm.dataloader.controller;

import com.acodexm.dataloader.entity.UserBalanceViewEntity;
import com.acodexm.dataloader.repository.UserBalanceViewRepository;
import com.acodexm.dataloader.service.PortfolioService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BalanceController {

  private final UserBalanceViewRepository userBalanceRepository;
  private final PortfolioService portfolioService;

  /** Get latest balances for a user */
  @GetMapping("/balances/{userId}")
  public ResponseEntity<?> getLatestBalances(@PathVariable String userId) {
    log.debug("Getting latest balances for user: {}", userId);

    List<UserBalanceViewEntity> balances = userBalanceRepository.findLatestBalancesByUserId(userId);

    if (balances.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(Map.of("message", "No balance data found", "userId", userId));
    }

    return ResponseEntity.ok(balances);
  }

  /** Get portfolio summary for a user */
  @GetMapping("/balances/summary/{userId}")
  public ResponseEntity<?> getPortfolioSummary(@PathVariable String userId) {
    log.debug("Getting portfolio summary for user: {}", userId);

    Map<String, Object> summary = portfolioService.getPortfolioSummary(userId);

    return ResponseEntity.ok(summary);
  }
}
