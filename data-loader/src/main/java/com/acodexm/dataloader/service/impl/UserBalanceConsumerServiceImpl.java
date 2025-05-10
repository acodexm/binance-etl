package com.acodexm.dataloader.service.impl;

import com.acodexm.dataloader.entity.UserBalanceViewEntity;
import com.acodexm.dataloader.entity.UserEntity;
import com.acodexm.dataloader.model.UserBalanceData;
import com.acodexm.dataloader.repository.UserBalanceViewRepository;
import com.acodexm.dataloader.repository.UserRepository;
import com.acodexm.dataloader.service.PortfolioService;
import com.acodexm.dataloader.service.UserBalanceConsumerService;
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
public class UserBalanceConsumerServiceImpl implements UserBalanceConsumerService {

  private final UserBalanceViewRepository userBalanceViewRepository;
  private final UserRepository userRepository;
  private final PortfolioService portfolioService;
  private final ObjectMapper objectMapper;

  @KafkaListener(
      topics = "${kafka.topics.user-balances}",
      groupId = "${spring.kafka.consumer.group-id}")
  @Transactional
  @Override
  public void consumeUserBalance(String message) {
    try {
      UserBalanceData balanceData = objectMapper.readValue(message, UserBalanceData.class);
      log.debug("Received user balance data: {}", balanceData);

      String userId = balanceData.getUserId();
      if (userId == null || userId.isEmpty()) {
        log.error("Received user balance data without userId, cannot process");
        return;
      }

      // Verify the user exists
      UserEntity user =
          userRepository
              .findByUserId(userId)
              .orElseGet(
                  () -> {
                    log.warn("User with ID {} not found, creating new user record", userId);
                    UserEntity newUser =
                        UserEntity.builder()
                            .userId(userId)
                            .createdAt(Instant.now())
                            .active(true)
                            .lastUpdated(Instant.now())
                            .build();
                    return userRepository.save(newUser);
                  });

      Instant timestamp =
          balanceData.getTimestamp() != null ? balanceData.getTimestamp() : Instant.now();

      // Create and save user balance view entity
      UserBalanceViewEntity entity =
          UserBalanceViewEntity.builder()
              .userId(userId)
              .asset(balanceData.getAsset())
              .free(balanceData.getFree())
              .locked(balanceData.getLocked())
              .totalAmount(balanceData.getFree().add(balanceData.getLocked()))
              .recordTimestamp(timestamp)
              .build();

      userBalanceViewRepository.save(entity);
      log.debug(
          "Saved user balance data to database for user: {}, asset: {}",
          userId,
          balanceData.getAsset());

      // Trigger portfolio value calculation
      portfolioService.calculatePortfolioValue(userId);

    } catch (Exception e) {
      log.error("Error processing user balance data: {}", e.getMessage(), e);
    }
  }
}
