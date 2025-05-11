package com.acodexm.dataloader.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.transaction.annotation.Transactional;

public interface UserConsumerService {
  @KafkaListener(
      topics = "${kafka.topics.user-data}",
      groupId = "${spring.kafka.consumer.group-id}")
  @Transactional
  void consumeUserData(String message);
}
