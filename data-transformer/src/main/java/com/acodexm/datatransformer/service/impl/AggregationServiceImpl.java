package com.acodexm.datatransformer.service.impl;

import com.acodexm.datatransformer.entity.AggregatedKlineEntity;
import com.acodexm.datatransformer.entity.RawKlineMirrorEntity;
import com.acodexm.datatransformer.mapper.DataMapper;
import com.acodexm.datatransformer.model.TimeMarketData;
import com.acodexm.datatransformer.model.TimeWindowType;
import com.acodexm.datatransformer.repository.AggregatedKlineRepository;
import com.acodexm.datatransformer.service.AggregationService;
import com.acodexm.datatransformer.service.KlineDataService;
import com.acodexm.datatransformer.service.TimeMarketDataPublisherService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AggregationServiceImpl implements AggregationService {

  private final KlineDataService klineDataService;
  private final AggregatedKlineRepository aggregatedRepository;
  private final TimeMarketDataPublisherService publisherService;
  private final DataMapper dataMapper;

  @Override
  @Scheduled(cron = "${aggregation.schedule}")
  @Transactional
  @CacheEvict(
      value = {"aggregatedDataCache"},
      allEntries = true)
  public void aggregateAllData() {
    log.info("Starting data aggregation for all symbols and windows");

    List<String> symbols = klineDataService.getAllSymbols();

    for (String symbol : symbols) {
      for (TimeWindowType windowType : TimeWindowType.values()) {
        try {
          aggregateForSymbolAndWindow(symbol, windowType);
        } catch (Exception e) {
          log.error(
              "Error aggregating data for symbol: {} and window: {}: {}",
              symbol,
              windowType.getValue(),
              e.getMessage(),
              e);
        }
      }
    }

    log.info("Data aggregation completed");
  }

  @Override
  @Transactional
  public AggregatedKlineEntity aggregateForSymbolAndWindow(
      String symbol, TimeWindowType windowType) {
    Instant now = Instant.now();
    Instant startTime;

    // Determine time window based on the window type
    switch (windowType) {
      case HOUR:
        startTime = now.minus(Duration.ofHours(1));
        break;
      case DAY:
        startTime = now.minus(Duration.ofDays(1));
        break;
      case WEEK:
        startTime = now.minus(Duration.ofDays(7));
        break;
      default:
        throw new IllegalArgumentException("Unsupported window type: " + windowType);
    }

    // Fetch raw kline data for the time window
    List<RawKlineMirrorEntity> klines =
        klineDataService.findBySymbolAndTimeRange(symbol, startTime, now);

    if (klines.isEmpty()) {
      log.info("No data found for symbol: {} and window: {}", symbol, windowType.getValue());
      return null;
    }

    // Calculate average price (using close prices for simplicity)
    BigDecimal averagePrice = calculateAveragePrice(klines);

    // Save aggregated data
    AggregatedKlineEntity aggregatedEntity =
        AggregatedKlineEntity.builder()
            .symbol(symbol)
            .windowType(windowType.getValue())
            .windowStartTime(startTime)
            .windowEndTime(now)
            .averagePrice(averagePrice)
            .build();

    AggregatedKlineEntity savedEntity = aggregatedRepository.save(aggregatedEntity);
    log.debug("Saved aggregated data for symbol: {} and window: {}", symbol, windowType.getValue());

    // Publish to Kafka
    TimeMarketData timeMarketData = dataMapper.toTimeMarketData(savedEntity);
    publisherService.publishTimeMarketData(timeMarketData);

    return savedEntity;
  }

  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "aggregatedDataCache", key = "{#symbol, #windowType}")
  public AggregatedKlineEntity getLatestAggregatedData(String symbol, TimeWindowType windowType) {
    Optional<AggregatedKlineEntity> latestData =
        aggregatedRepository.findFirstBySymbolAndWindowTypeOrderByWindowStartTimeDesc(
            symbol, windowType.getValue());
    return latestData.orElse(null);
  }

  @Override
  @Transactional(readOnly = true)
  @Cacheable(
      value = "aggregatedDataRangeCache",
      key = "{#symbol, #windowType, #startTime, #endTime}")
  public List<AggregatedKlineEntity> getAggregatedDataInTimeRange(
      String symbol, TimeWindowType windowType, Instant startTime, Instant endTime) {
    return aggregatedRepository
        .findBySymbolAndWindowTypeAndWindowStartTimeBetweenOrderByWindowStartTimeAsc(
            symbol, windowType.getValue(), startTime, endTime);
  }

  private BigDecimal calculateAveragePrice(List<RawKlineMirrorEntity> klines) {
    if (klines.isEmpty()) {
      return BigDecimal.ZERO;
    }

    BigDecimal sum =
        klines.stream()
            .map(RawKlineMirrorEntity::getClose)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    return sum.divide(BigDecimal.valueOf(klines.size()), 8, RoundingMode.HALF_UP);
  }
}
