package com.acodexm.binanceconnector.services;

import com.acodexm.binanceconnector.domain.KlineData;
import com.acodexm.binanceconnector.domain.UserBalance;

/** Service for publishing market data to Kafka topics */
public interface MarketDataPublisherService {

  /**
   * Publishes kline data from REST API to Kafka
   *
   * @param klineData The kline data to publish
   */
  void publishKlineData(KlineData klineData);

  /**
   * Publishes user balance data to Kafka
   *
   * @param userBalance The user balance to publish
   */
  void publishUserBalance(UserBalance userBalance);
}
