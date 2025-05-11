package com.acodexm.binanceconnector.services.impl;

import com.acodexm.binanceconnector.config.KafkaConfig;
import com.acodexm.binanceconnector.domain.KlineData;
import com.acodexm.binanceconnector.domain.User;
import com.acodexm.binanceconnector.domain.UserBalance;
import com.acodexm.binanceconnector.services.MarketDataPublisherService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketDataPublisherServiceImpl implements MarketDataPublisherService {

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;
  private final KafkaConfig kafkaConfig;

  @Override
  public void publishKlineData(KlineData klineData) {
    try {
      String message = objectMapper.writeValueAsString(klineData);
      kafkaTemplate.send(kafkaConfig.getKlineData(),  message);
      log.debug("Published kline data for symbol: {}", klineData.getSymbol());
    } catch (JsonProcessingException e) {
      log.error("Failed to publish kline data: {}", e.getMessage(), e);
    }
  }

  @Override
  public void publishUserBalance(UserBalance userBalance) {
    try {
      String message = objectMapper.writeValueAsString(userBalance);
      kafkaTemplate.send(kafkaConfig.getUserBalance(),  message);
      log.debug("Published user balance for asset: {}", userBalance.getAsset());
    } catch (JsonProcessingException e) {
      log.error("Failed to publish user balance: {}", e.getMessage(), e);
    }
  }

  @Override
  public void publishUserData(User user) {
    try {
      String message = objectMapper.writeValueAsString(user);
      kafkaTemplate.send(kafkaConfig.getUserData(),  message);
      log.debug("Published user: {}", user);
    } catch (JsonProcessingException e) {
      log.error("Failed to publish user: {}", e.getMessage(), e);
    }
  }
}
