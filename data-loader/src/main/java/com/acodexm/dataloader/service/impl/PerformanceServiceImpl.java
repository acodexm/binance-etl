package com.acodexm.dataloader.service.impl;

import com.acodexm.dataloader.entity.SymbolPerformanceViewEntity;
import com.acodexm.dataloader.repository.SymbolPerformanceViewRepository;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.acodexm.dataloader.service.PerformanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PerformanceServiceImpl implements PerformanceService {

  private final SymbolPerformanceViewRepository performanceRepository;

  /** Get the latest performance data for a symbol and window type */
  @Transactional(readOnly = true)
  @Override
  public Optional<SymbolPerformanceViewEntity> getLatestPerformance(
      String symbol, String windowType) {
    return performanceRepository.findFirstBySymbolAndWindowTypeOrderByWindowStartTimeDesc(
        symbol, windowType);
  }

  /** Get the latest performance data for all window types for a symbol */
  @Transactional(readOnly = true)
  @Override
  public Map<String, SymbolPerformanceViewEntity> getLatestPerformanceForAllWindows(String symbol) {
    List<SymbolPerformanceViewEntity> performances =
        performanceRepository.findLatestPerformanceForAllWindowsBySymbol(symbol);

    Map<String, SymbolPerformanceViewEntity> result = new HashMap<>();
    for (SymbolPerformanceViewEntity performance : performances) {
      result.put(performance.getWindowType(), performance);
    }

    return result;
  }

  /** Get historical performance data for a symbol and window type */
  @Transactional(readOnly = true)
  @Override
  public List<SymbolPerformanceViewEntity> getHistoricalPerformance(
      String symbol, String windowType, Instant startTime, Instant endTime) {

    return performanceRepository
        .findBySymbolAndWindowTypeAndWindowStartTimeBetweenOrderByWindowStartTimeAsc(
            symbol, windowType, startTime, endTime);
  }

  /** Check if performance data exists for the given symbol and window */
  @Transactional(readOnly = true)
  @Override
  public boolean hasPerformanceData(String symbol, String windowType) {
    return getLatestPerformance(symbol, windowType).isPresent();
  }
}
