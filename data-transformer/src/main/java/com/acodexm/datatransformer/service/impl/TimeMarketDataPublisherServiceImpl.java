package com.acodexm.datatransformer.service.impl;

import com.acodexm.datatransformer.config.KafkaTopicConfig;
import com.acodexm.datatransformer.model.TimeMarketData;
import com.acodexm.datatransformer.service.TimeMarketDataPublisherService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TimeMarketDataPublisherServiceImpl implements TimeMarketDataPublisherService {

  private final KafkaTemplate<String, Object> kafkaTemplate;
  private final ObjectMapper objectMapper;
  private final KafkaTopicConfig kafkaTopicConfig;

  @Override
  public void publishTimeMarketData(TimeMarketData timeMarketData) {
    try {
      String message = objectMapper.writeValueAsString(timeMarketData);
      kafkaTemplate.send(kafkaTopicConfig.getTimeMarketData(), timeMarketData.getSymbol(), message);
      log.debug(
          "Published time market data for symbol: {} and window: {}",
          timeMarketData.getSymbol(),
          timeMarketData.getWindow());
    } catch (JsonProcessingException e) {
      log.error("Failed to publish time market data: {}", e.getMessage(), e);
    }
  }
}
