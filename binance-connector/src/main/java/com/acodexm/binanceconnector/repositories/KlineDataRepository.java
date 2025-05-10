package com.acodexm.binanceconnector.repositories;

import com.acodexm.binanceconnector.entities.KlineDataEntity;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KlineDataRepository extends JpaRepository<KlineDataEntity, Long> {
  List<KlineDataEntity> findBySymbolAndIntervalOrderByOpenTimeDesc(String symbol, String interval);

  List<KlineDataEntity> findBySymbolAndIntervalAndOpenTimeBetween(
      String symbol, String interval, Instant start, Instant end);

  List<KlineDataEntity> findBySymbolInAndInterval(List<String> symbols, String interval);
}
