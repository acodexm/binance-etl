package com.acodexm.dataloader.service;

import com.acodexm.dataloader.entity.SymbolPerformanceViewEntity;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PerformanceService {
    @Transactional(readOnly = true)
    Optional<SymbolPerformanceViewEntity> getLatestPerformance(
        String symbol, String windowType);

    @Transactional(readOnly = true)
    Map<String, SymbolPerformanceViewEntity> getLatestPerformanceForAllWindows(String symbol);

    @Transactional(readOnly = true)
    List<SymbolPerformanceViewEntity> getHistoricalPerformance(
        String symbol, String windowType, Instant startTime, Instant endTime);

    @Transactional(readOnly = true)
    boolean hasPerformanceData(String symbol, String windowType);
}
