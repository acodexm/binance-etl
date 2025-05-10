package com.acodexm.binanceconnector.services.impl;

import com.acodexm.binanceconnector.usecases.MarketDataFetchUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@EnableScheduling
@RequiredArgsConstructor
public class MarketDataSchedulerService {

  private final MarketDataFetchUseCase marketDataFetchUseCase;

  /**
   * Scheduled task to fetch kline data at regular intervals Uses the fixed rate from configuration
   */
  @Scheduled(fixedRateString = "${binance.api.fetchIntervalSeconds:60}000")
  public void scheduledFetchKlineData() {
    log.info("Running scheduled market data fetch");
    marketDataFetchUseCase.execute();
  }
}
