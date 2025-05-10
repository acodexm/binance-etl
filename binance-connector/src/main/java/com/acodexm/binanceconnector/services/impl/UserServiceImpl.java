package com.acodexm.binanceconnector.services.impl;

import com.acodexm.binanceconnector.domain.User;
import com.acodexm.binanceconnector.entities.UserEntity;
import com.acodexm.binanceconnector.mappers.UserMapper;
import com.acodexm.binanceconnector.repositories.UserRepository;
import com.acodexm.binanceconnector.services.BinanceApiService;
import com.acodexm.binanceconnector.services.UserService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final BinanceApiService binanceApiService;

  @Override
  public User getCurrentUser() {
    return userRepository
        .findActiveUser()
        .map(userMapper::toDomain)
        .orElseGet(this::getAndSaveNewUser);
  }

  @Override
  public Optional<User> findByUserId(String userId) {
    return userRepository.findByUserId(userId).map(userMapper::toDomain);
  }

  private User getAndSaveNewUser() {
    User newUser = binanceApiService.fetchUser();
    UserEntity userEntity = userMapper.toEntity(newUser);
    userRepository.save(userEntity);
    log.info("Created new user with ID: {}", userEntity.getUserId());
    return newUser;
  }
}
