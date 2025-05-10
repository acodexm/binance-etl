package com.acodexm.dataloader.service.impl;

import com.acodexm.dataloader.entity.SymbolPerformanceViewEntity;
import com.acodexm.dataloader.entity.SymbolTimeWindowPriceEntity;
import com.acodexm.dataloader.model.TimeMarketData;
import com.acodexm.dataloader.repository.SymbolPerformanceViewRepository;
import com.acodexm.dataloader.repository.SymbolTimeWindowPriceRepository;
import com.acodexm.dataloader.service.PortfolioService;
import com.acodexm.dataloader.service.TimeMarketDataConsumerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimeMarketDataConsumerServiceImpl implements TimeMarketDataConsumerService {

  private final SymbolTimeWindowPriceRepository timeWindowPriceRepository;
  private final SymbolPerformanceViewRepository performanceViewRepository;
  private final PortfolioService portfolioService;
  private final ObjectMapper objectMapper;

  @KafkaListener(
      topics = "${kafka.topics.time-market-data}",
      groupId = "${spring.kafka.consumer.group-id}")
  @Transactional
  @Override
  public void consumeTimeMarketData(String message) {
    try {
      TimeMarketData marketData = objectMapper.readValue(message, TimeMarketData.class);
      log.debug("Received time market data: {}", marketData);

      // Store in symbol_time_window_prices table
      SymbolTimeWindowPriceEntity timeWindowEntity =
          SymbolTimeWindowPriceEntity.builder()
              .symbol(marketData.getSymbol())
              .windowType(marketData.getWindow())
              .windowStartTime(marketData.getStartTime())
              .windowEndTime(marketData.getEndTime())
              .averagePrice(marketData.getAveragePrice())
              .recordTimestamp(Instant.now())
              .build();

      timeWindowPriceRepository.save(timeWindowEntity);
      log.debug(
          "Saved time window price data for symbol: {} and window: {}",
          marketData.getSymbol(),
          marketData.getWindow());

      // Calculate gain/loss and store in symbol_performance_view table
      calculateAndSavePerformance(marketData);

      // Trigger portfolio value recalculation since market price has changed
      portfolioService.recalculateAllPortfolios();

    } catch (Exception e) {
      log.error("Error processing time market data: {}", e.getMessage(), e);
    }
  }

  private void calculateAndSavePerformance(TimeMarketData currentData) {
    // Get previous time window data for comparison
    // For the same symbol and window type but earlier time window
    Optional<SymbolTimeWindowPriceEntity> previousEntityOpt;

    if ("1h".equals(currentData.getWindow())) {
      // For 1h window, get the previous hour
      previousEntityOpt =
          findPreviousWindowPrice(
              currentData.getSymbol(),
              currentData.getWindow(),
              currentData.getStartTime(),
              Duration.ofHours(1));
    } else if ("1d".equals(currentData.getWindow())) {
      // For 1d window, get the previous day
      previousEntityOpt =
          findPreviousWindowPrice(
              currentData.getSymbol(),
              currentData.getWindow(),
              currentData.getStartTime(),
              Duration.ofDays(1));
    } else if ("1w".equals(currentData.getWindow())) {
      // For 1w window, get the previous week
      previousEntityOpt =
          findPreviousWindowPrice(
              currentData.getSymbol(),
              currentData.getWindow(),
              currentData.getStartTime(),
              Duration.ofDays(7));
    } else {
      log.warn(
          "Unsupported window type: {}, skipping performance calculation", currentData.getWindow());
      return;
    }

    // Calculate gain/loss percentage
    BigDecimal previousPrice =
        previousEntityOpt.map(SymbolTimeWindowPriceEntity::getAveragePrice).orElse(null);

    BigDecimal gainLossPercent = null;

    if (previousPrice != null && previousPrice.compareTo(BigDecimal.ZERO) > 0) {
      // (current - previous) / previous * 100
      gainLossPercent =
          currentData
              .getAveragePrice()
              .subtract(previousPrice)
              .divide(previousPrice, 8, RoundingMode.HALF_UP)
              .multiply(new BigDecimal("100"));
    }

    // Create and save performance entity
    SymbolPerformanceViewEntity performanceEntity =
        SymbolPerformanceViewEntity.builder()
            .symbol(currentData.getSymbol())
            .windowType(currentData.getWindow())
            .windowStartTime(currentData.getStartTime())
            .windowEndTime(currentData.getEndTime())
            .averagePrice(currentData.getAveragePrice())
            .previousAveragePrice(previousPrice)
            .gainLossPercent(gainLossPercent)
            .recordTimestamp(Instant.now())
            .build();

    performanceViewRepository.save(performanceEntity);

    log.debug(
        "Saved performance data for symbol: {}, window: {}, gain/loss: {}%",
        currentData.getSymbol(),
        currentData.getWindow(),
        gainLossPercent != null ? gainLossPercent.setScale(2, RoundingMode.HALF_UP) : "N/A");
  }

  private Optional<SymbolTimeWindowPriceEntity> findPreviousWindowPrice(
      String symbol, String windowType, Instant currentStartTime, Duration windowDuration) {

    List<SymbolTimeWindowPriceEntity> previousPrices =
        timeWindowPriceRepository.findPreviousPricesForWindow(symbol, windowType, currentStartTime);

    if (previousPrices.isEmpty()) {
      return Optional.empty();
    }

    // Find the price with timestamp closest to (currentStartTime - windowDuration)
    Instant targetTime = currentStartTime.minus(windowDuration);

    return previousPrices.stream()
        .min(
            (a, b) -> {
              Duration durationA = Duration.between(a.getWindowStartTime(), targetTime).abs();
              Duration durationB = Duration.between(b.getWindowStartTime(), targetTime).abs();
              return durationA.compareTo(durationB);
            });
  }
}
