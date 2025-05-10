package com.acodexm.dataloader.controller;

import com.acodexm.dataloader.repository.SymbolTimeWindowPriceRepository;
import com.acodexm.dataloader.repository.UserBalanceViewRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/status")
@RequiredArgsConstructor
public class StatusController {

  private final SymbolTimeWindowPriceRepository timeWindowPriceRepository;
  private final UserBalanceViewRepository userBalanceRepository;

  @GetMapping
  public ResponseEntity<Map<String, Object>> getServiceStatus() {
    Map<String, Object> status = new HashMap<>();
    status.put("service", "data-loader");
    status.put("status", "UP");
    status.put("timestamp", System.currentTimeMillis());

    // Get available symbols
    List<String> symbols = timeWindowPriceRepository.findDistinctSymbols();
    status.put("availableSymbols", symbols);
    status.put("symbolsCount", symbols.size());

    // Get available assets
    List<String> assets = userBalanceRepository.findDistinctAssetsByUserId("default");
    status.put("availableAssets", assets);
    status.put("assetsCount", assets.size());

    return ResponseEntity.ok(status);
  }
}
