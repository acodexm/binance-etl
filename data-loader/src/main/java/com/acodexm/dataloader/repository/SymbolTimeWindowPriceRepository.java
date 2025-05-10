package com.acodexm.dataloader.repository;

import com.acodexm.dataloader.entity.SymbolTimeWindowPriceEntity;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SymbolTimeWindowPriceRepository
    extends JpaRepository<SymbolTimeWindowPriceEntity, Long> {

  List<SymbolTimeWindowPriceEntity> findBySymbolAndWindowTypeOrderByWindowStartTimeDesc(
      String symbol, String windowType);

  Optional<SymbolTimeWindowPriceEntity> findFirstBySymbolAndWindowTypeOrderByWindowStartTimeDesc(
      String symbol, String windowType);

  @Query(
      "SELECT e FROM SymbolTimeWindowPriceEntity e WHERE e.symbol = ?1 AND e.windowType = ?2 "
          + "AND e.windowStartTime < ?3 ORDER BY e.windowStartTime DESC")
  List<SymbolTimeWindowPriceEntity> findPreviousPricesForWindow(
      String symbol, String windowType, Instant currentStartTime);

  List<SymbolTimeWindowPriceEntity>
      findBySymbolAndWindowTypeAndWindowStartTimeBetweenOrderByWindowStartTimeAsc(
          String symbol, String windowType, Instant startTime, Instant endTime);

  @Query("SELECT DISTINCT e.symbol FROM SymbolTimeWindowPriceEntity e")
  List<String> findDistinctSymbols();

  @Query("SELECT DISTINCT e.windowType FROM SymbolTimeWindowPriceEntity e WHERE e.symbol = ?1")
  List<String> findDistinctWindowTypesBySymbol(String symbol);
}
