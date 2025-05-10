package com.acodexm.dataloader.repository;

import com.acodexm.dataloader.entity.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {

  Optional<UserEntity> findByUserId(String userId);

  @Query("SELECT u FROM UserEntity u WHERE u.active = true")
  Optional<UserEntity> findActiveUser();
}
