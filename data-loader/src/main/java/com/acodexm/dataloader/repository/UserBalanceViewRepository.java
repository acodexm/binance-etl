package com.acodexm.dataloader.repository;

import com.acodexm.dataloader.entity.UserBalanceViewEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserBalanceViewRepository extends JpaRepository<UserBalanceViewEntity, Long> {

  List<UserBalanceViewEntity> findByUserIdOrderByRecordTimestampDesc(String userId);

  List<UserBalanceViewEntity> findByUserIdAndAssetOrderByRecordTimestampDesc(
      String userId, String asset);

  @Query("SELECT DISTINCT e.asset FROM UserBalanceViewEntity e WHERE e.userId = ?1")
  List<String> findDistinctAssetsByUserId(String userId);

  List<UserBalanceViewEntity> findTopByUserIdAndAssetOrderByRecordTimestampDesc(
      String userId, String asset);

  @Query(
      "SELECT e FROM UserBalanceViewEntity e WHERE e.userId = ?1 AND e.recordTimestamp = "
          + "(SELECT MAX(e2.recordTimestamp) FROM UserBalanceViewEntity e2 WHERE e2.userId = e.userId)")
  List<UserBalanceViewEntity> findLatestBalancesByUserId(String userId);
}
