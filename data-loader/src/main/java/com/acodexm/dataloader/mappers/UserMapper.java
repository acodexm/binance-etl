package com.acodexm.dataloader.mappers;

import com.acodexm.dataloader.entity.UserEntity;
import com.acodexm.dataloader.model.UserData;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring")
public interface UserMapper {

  /**
   * Maps UserData to UserEntity
   *
   * @param userData the UserData to map
   * @return the mapped UserEntity
   */
  UserEntity toEntity(UserData userData);

  /**
   * Maps UserEntity to UserData
   *
   * @param userEntity the UserEntity to map
   * @return the mapped UserData
   */
  UserData toData(UserEntity userEntity);
}
