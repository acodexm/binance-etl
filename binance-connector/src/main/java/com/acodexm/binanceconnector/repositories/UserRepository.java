package com.acodexm.binanceconnector.repositories;

import com.acodexm.binanceconnector.entities.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {

  Optional<UserEntity> findByUserId(String userId);

  @Query("SELECT u FROM UserEntity u WHERE u.active = true ORDER BY u.lastUpdated DESC")
  Optional<UserEntity> findActiveUser();
}
