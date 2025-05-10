package com.acodexm.dataloader.repository;

import com.acodexm.dataloader.entity.SymbolPerformanceViewEntity;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SymbolPerformanceViewRepository
    extends JpaRepository<SymbolPerformanceViewEntity, Long> {

  Optional<SymbolPerformanceViewEntity> findFirstBySymbolAndWindowTypeOrderByWindowStartTimeDesc(
      String symbol, String windowType);

  List<SymbolPerformanceViewEntity> findBySymbolAndWindowTypeOrderByWindowStartTimeDesc(
      String symbol, String windowType);

  List<SymbolPerformanceViewEntity>
      findBySymbolAndWindowTypeAndWindowStartTimeBetweenOrderByWindowStartTimeAsc(
          String symbol, String windowType, Instant startTime, Instant endTime);

  @Query(
      "SELECT e FROM SymbolPerformanceViewEntity e WHERE e.symbol = ?1 "
          + "GROUP BY e.windowType, e.id HAVING e.windowStartTime = MAX(e.windowStartTime)")
  List<SymbolPerformanceViewEntity> findLatestPerformanceForAllWindowsBySymbol(String symbol);
}
