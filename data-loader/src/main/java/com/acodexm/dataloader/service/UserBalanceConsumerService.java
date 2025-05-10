package com.acodexm.dataloader.service;

import com.acodexm.dataloader.entity.UserBalanceViewEntity;
import com.acodexm.dataloader.model.UserBalanceData;
import com.acodexm.dataloader.repository.UserBalanceViewRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserBalanceConsumerService {

  private final UserBalanceViewRepository userBalanceViewRepository;
  private final PortfolioService portfolioService;
  private final ObjectMapper objectMapper;

  @KafkaListener(
      topics = "${kafka.topics.user-balances}",
      groupId = "${spring.kafka.consumer.group-id}")
  @Transactional
  public void consumeUserBalance(String message) {
    try {
      UserBalanceData balanceData = objectMapper.readValue(message, UserBalanceData.class);
      log.debug("Received user balance data: {}", balanceData);

      Instant timestamp =
          balanceData.getTimestamp() != null ? balanceData.getTimestamp() : Instant.now();

      // Create and save user balance view entity
      UserBalanceViewEntity entity =
          UserBalanceViewEntity.builder()
              .userId(balanceData.getUserId())
              .asset(balanceData.getAsset())
              .free(balanceData.getFree())
              .locked(balanceData.getLocked())
              .totalAmount(balanceData.getFree().add(balanceData.getLocked()))
              .recordTimestamp(timestamp)
              .build();

      userBalanceViewRepository.save(entity);
      log.debug("Saved user balance data to database for asset: {}", balanceData.getAsset());

      // Trigger portfolio value calculation
      portfolioService.calculatePortfolioValue(balanceData.getUserId());

    } catch (Exception e) {
      log.error("Error processing user balance data: {}", e.getMessage(), e);
    }
  }
}
