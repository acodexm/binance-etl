package com.acodexm.binanceconnector.mappers;

import com.acodexm.binanceconnector.domain.User;
import com.acodexm.binanceconnector.entities.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

  /**
   * Maps a UserEntity to a User domain object
   *
   * @param entity the user entity to map
   * @return the mapped User domain object
   */
  User toDomain(UserEntity entity);

  /**
   * Maps a User domain object to a UserEntity
   *
   * @param domain the User domain object to map
   * @return the mapped UserEntity
   */
  UserEntity toEntity(User domain);
}
