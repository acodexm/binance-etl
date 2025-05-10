package com.acodexm.datatransformer.repository;

import com.acodexm.datatransformer.entity.AggregatedKlineEntity;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AggregatedKlineRepository extends JpaRepository<AggregatedKlineEntity, Long> {

  List<AggregatedKlineEntity> findBySymbolAndWindowTypeOrderByWindowStartTimeDesc(
      String symbol, String windowType);

  Optional<AggregatedKlineEntity> findFirstBySymbolAndWindowTypeOrderByWindowStartTimeDesc(
      String symbol, String windowType);

  List<AggregatedKlineEntity>
      findBySymbolAndWindowTypeAndWindowStartTimeBetweenOrderByWindowStartTimeAsc(
          String symbol, String windowType, Instant startTime, Instant endTime);
}
