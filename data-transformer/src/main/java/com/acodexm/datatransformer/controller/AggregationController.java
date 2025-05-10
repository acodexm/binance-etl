package com.acodexm.datatransformer.controller;

import com.acodexm.datatransformer.entity.AggregatedKlineEntity;
import com.acodexm.datatransformer.exception.ResourceNotFoundException;
import com.acodexm.datatransformer.mapper.DataMapper;
import com.acodexm.datatransformer.model.TimeMarketData;
import com.acodexm.datatransformer.model.TimeWindowType;
import com.acodexm.datatransformer.service.AggregationService;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/aggregation")
@RequiredArgsConstructor
public class AggregationController {

  private final AggregationService aggregationService;
  private final DataMapper dataMapper;

  @PostMapping("/trigger")
  public ResponseEntity<String> triggerAggregation() {
    aggregationService.aggregateAllData();
    return ResponseEntity.ok("Aggregation triggered successfully");
  }

  @GetMapping("/{symbol}/{windowType}")
  public ResponseEntity<TimeMarketData> getLatestAggregatedData(
      @PathVariable String symbol, @PathVariable String windowType) {

    TimeWindowType window;
    try {
      window = TimeWindowType.fromValue(windowType);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid window type: " + windowType);
    }

    AggregatedKlineEntity entity = aggregationService.getLatestAggregatedData(symbol, window);
    if (entity == null) {
      throw new ResourceNotFoundException(
          "Aggregated data", "symbol and window", symbol + " - " + windowType);
    }

    TimeMarketData marketData = dataMapper.toTimeMarketData(entity);
    return ResponseEntity.ok(marketData);
  }

  @GetMapping("/{symbol}/{windowType}/history")
  public ResponseEntity<List<TimeMarketData>> getAggregatedDataHistory(
      @PathVariable String symbol,
      @PathVariable String windowType,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startTime,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endTime) {

    TimeWindowType window;
    try {
      window = TimeWindowType.fromValue(windowType);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid window type: " + windowType);
    }

    List<AggregatedKlineEntity> entities =
        aggregationService.getAggregatedDataInTimeRange(symbol, window, startTime, endTime);

    List<TimeMarketData> result =
        entities.stream().map(dataMapper::toTimeMarketData).collect(Collectors.toList());

    return ResponseEntity.ok(result);
  }

  @PostMapping("/{symbol}/{windowType}")
  public ResponseEntity<TimeMarketData> aggregateSingleSymbolAndWindow(
      @PathVariable String symbol, @PathVariable String windowType) {

    TimeWindowType window;
    try {
      window = TimeWindowType.fromValue(windowType);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid window type: " + windowType);
    }

    AggregatedKlineEntity entity = aggregationService.aggregateForSymbolAndWindow(symbol, window);
    if (entity == null) {
      throw new ResourceNotFoundException(
          "No raw data found for aggregation", "symbol and window", symbol + " - " + windowType);
    }

    TimeMarketData marketData = dataMapper.toTimeMarketData(entity);
    return ResponseEntity.ok(marketData);
  }
}
