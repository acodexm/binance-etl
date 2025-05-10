package com.acodexm.dataloader.controller;

import com.acodexm.dataloader.entity.SymbolPerformanceViewEntity;
import com.acodexm.dataloader.service.PerformanceService;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PerformanceController {

  private final PerformanceService performanceService;

  /** Get latest performance for a symbol and window */
  @GetMapping("/performance/{symbol}/{window}")
  public ResponseEntity<?> getLatestPerformance(
      @PathVariable String symbol, @PathVariable String window) {

    log.debug("Getting latest performance for symbol: {} and window: {}", symbol, window);

    Optional<SymbolPerformanceViewEntity> performance =
        performanceService.getLatestPerformance(symbol, window);

    if (performance.isPresent()) {
      return ResponseEntity.ok(performance.get());
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(
              Map.of(
                  "message", "No performance data found",
                  "symbol", symbol,
                  "window", window));
    }
  }

  /** Get latest performance for all window types for a symbol */
  @GetMapping("/performance/{symbol}")
  public ResponseEntity<?> getLatestPerformanceForAllWindows(@PathVariable String symbol) {
    log.debug("Getting latest performance for all windows for symbol: {}", symbol);

    Map<String, SymbolPerformanceViewEntity> performances =
        performanceService.getLatestPerformanceForAllWindows(symbol);

    if (performances.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(Map.of("message", "No performance data found", "symbol", symbol));
    }

    return ResponseEntity.ok(performances);
  }

  /** Get historical performance data for a symbol and window */
  @GetMapping("/history/performance/{symbol}/{window}")
  public ResponseEntity<?> getHistoricalPerformance(
      @PathVariable String symbol,
      @PathVariable String window,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startTime,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endTime) {

    log.debug(
        "Getting historical performance for symbol: {} and window: {} from {} to {}",
        symbol,
        window,
        startTime,
        endTime);

    List<SymbolPerformanceViewEntity> performances =
        performanceService.getHistoricalPerformance(symbol, window, startTime, endTime);

    if (performances.isEmpty()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(
              Map.of(
                  "message", "No historical performance data found",
                  "symbol", symbol,
                  "window", window,
                  "startTime", startTime,
                  "endTime", endTime));
    }

    return ResponseEntity.ok(performances);
  }
}
