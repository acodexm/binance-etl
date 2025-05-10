package com.acodexm.datatransformer.service;

import com.acodexm.datatransformer.entity.RawKlineMirrorEntity;
import com.acodexm.datatransformer.model.RawKlineData;
import java.time.Instant;
import java.util.List;

public interface KlineDataService {

  /**
   * Save raw kline data to the database
   *
   * @param rawKlineData the raw kline data to save
   * @return the saved entity
   */
  RawKlineMirrorEntity saveRawKlineData(RawKlineData rawKlineData);

  /**
   * Find kline data for a specific symbol within a time range
   *
   * @param symbol the trading symbol
   * @param startTime the start time
   * @param endTime the end time
   * @return list of kline entities
   */
  List<RawKlineMirrorEntity> findBySymbolAndTimeRange(
      String symbol, Instant startTime, Instant endTime);

  /**
   * Get all unique symbols available in the database
   *
   * @return list of symbols
   */
  List<String> getAllSymbols();
}
