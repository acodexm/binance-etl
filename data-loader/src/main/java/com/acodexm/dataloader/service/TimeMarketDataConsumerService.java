package com.acodexm.dataloader.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.transaction.annotation.Transactional;

public interface TimeMarketDataConsumerService {
  @KafkaListener(
      topics = "${kafka.topics.time-market-data}",
      groupId = "${spring.kafka.consumer.group-id}")
  @Transactional
  void consumeTimeMarketData(String message);
}
