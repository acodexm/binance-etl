package com.acodexm.binanceconnector.repositories;

import com.acodexm.binanceconnector.entities.UserBalanceEntity;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBalanceRepository extends JpaRepository<UserBalanceEntity, Long> {
  List<UserBalanceEntity> findByAssetAndTimestampBetween(String asset, Instant start, Instant end);

  List<UserBalanceEntity> findByAssetOrderByTimestampDesc(String asset);
}
