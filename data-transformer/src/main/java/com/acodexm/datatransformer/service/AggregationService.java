package com.acodexm.datatransformer.service;

import com.acodexm.datatransformer.entity.AggregatedKlineEntity;
import com.acodexm.datatransformer.model.TimeWindowType;
import java.time.Instant;
import java.util.List;

public interface AggregationService {

  /** Trigger data aggregation for all symbols and window types */
  void aggregateAllData();

  /**
   * Aggregate data for a specific symbol and window type
   *
   * @param symbol the trading symbol
   * @param windowType the time window type
   * @return the aggregated data entity
   */
  AggregatedKlineEntity aggregateForSymbolAndWindow(String symbol, TimeWindowType windowType);

  /**
   * Get latest aggregated data for a symbol and window type
   *
   * @param symbol the trading symbol
   * @param windowType the time window type
   * @return the aggregated data entity or null if not found
   */
  AggregatedKlineEntity getLatestAggregatedData(String symbol, TimeWindowType windowType);

  /**
   * Get aggregated data for a symbol and window type within a time range
   *
   * @param symbol the trading symbol
   * @param windowType the time window type
   * @param startTime the start time
   * @param endTime the end time
   * @return list of aggregated data entities
   */
  List<AggregatedKlineEntity> getAggregatedDataInTimeRange(
      String symbol, TimeWindowType windowType, Instant startTime, Instant endTime);
}
