package com.acodexm.binanceconnector.mappers;

import com.acodexm.binanceconnector.domain.User;
import com.acodexm.binanceconnector.entities.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

  public User toDomain(UserEntity entity) {
    if (entity == null) {
      return null;
    }

    return User.builder()
        .userId(entity.getUserId())
        .active(entity.isActive())
        .createdAt(entity.getCreatedAt())
        .lastUpdated(entity.getLastUpdated())
        .build();
  }

  public UserEntity toEntity(User domain) {
    if (domain == null) {
      return null;
    }

    return UserEntity.builder()
        .userId(domain.getUserId())
        .active(domain.isActive())
        .createdAt(domain.getCreatedAt())
        .lastUpdated(domain.getLastUpdated())
        .build();
  }
}
