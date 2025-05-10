package com.acodexm.datatransformer.controller;

import com.acodexm.datatransformer.service.KlineDataService;
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

  private final KlineDataService klineDataService;

  @GetMapping
  public ResponseEntity<Map<String, Object>> getServiceStatus() {
    Map<String, Object> status = new HashMap<>();
    status.put("service", "data-transformer");
    status.put("status", "UP");
    status.put("timestamp", System.currentTimeMillis());

    List<String> symbols = klineDataService.getAllSymbols();
    status.put("availableSymbols", symbols);
    status.put("symbolsCount", symbols.size());

    return ResponseEntity.ok(status);
  }
}
