package com.acodexm.dataloader.service.impl;

import com.acodexm.dataloader.entity.UserEntity;
import com.acodexm.dataloader.mappers.UserMapper;
import com.acodexm.dataloader.model.UserData;
import com.acodexm.dataloader.repository.UserRepository;
import com.acodexm.dataloader.service.UserConsumerService;
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
public class UserConsumerServiceImpl implements UserConsumerService {

  private final UserRepository userRepository;
  private final ObjectMapper objectMapper;
  private final UserMapper userMapper;

  @KafkaListener(
      topics = "${kafka.topics.user-data}",
      groupId = "${spring.kafka.consumer.group-id}")
  @Transactional
  @Override
  public void consumeUserData(String message) {
    try {
      UserData userData = objectMapper.readValue(message, UserData.class);
      log.debug("Received user data: {}", userData);

      userRepository
          .findByUserId(userData.getUserId())
          .ifPresentOrElse(
              existingUser -> {
                existingUser.setActive(userData.isActive());
                existingUser.setLastUpdated(Instant.now());
                userRepository.save(existingUser);
                log.debug("Updated existing user: {}", existingUser.getUserId());
              },
              () -> {
                UserEntity userEntity = userMapper.toEntity(userData);
                userRepository.save(userEntity);
                log.debug("Created new user: {}", userEntity.getUserId());
              });
    } catch (Exception e) {
      log.error("Error processing user data: {}", e.getMessage(), e);
    }
  }
}
