package com.acodexm.datatransformer.service.impl;

import com.acodexm.datatransformer.model.RawKlineData;
import com.acodexm.datatransformer.service.KlineDataConsumerService;
import com.acodexm.datatransformer.service.KlineDataService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KlineDataConsumerServiceImpl implements KlineDataConsumerService {

  private final KlineDataService klineDataService;
  private final ObjectMapper objectMapper;

  @KafkaListener(topics = "${kafka.topics.raw-kline-data}")
  public void consumeKlineData(String message) {
    try {
      RawKlineData rawKlineData = objectMapper.readValue(message, RawKlineData.class);
      log.debug("Received raw kline data: {}", rawKlineData);

      // Save the received kline data to database
      klineDataService.saveRawKlineData(rawKlineData);
      log.debug("Saved raw kline data to database for symbol: {}", rawKlineData.getSymbol());
    } catch (Exception e) {
      log.error("Error processing raw kline data: {}", e.getMessage(), e);
    }
  }
}
