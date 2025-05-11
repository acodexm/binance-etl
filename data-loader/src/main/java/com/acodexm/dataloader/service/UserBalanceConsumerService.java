package com.acodexm.dataloader.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.transaction.annotation.Transactional;

public interface UserBalanceConsumerService {
  @KafkaListener(
      topics = "${kafka.topics.user-balances}",
      groupId = "${spring.kafka.consumer.group-id}")
  @Transactional
  void consumeUserBalance(String message);
}
