package com.acodexm.datatransformer.repository;

import com.acodexm.datatransformer.entity.RawKlineMirrorEntity;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RawKlineMirrorRepository extends JpaRepository<RawKlineMirrorEntity, Long> {

  List<RawKlineMirrorEntity> findBySymbolAndOpenTimeBetweenOrderByOpenTimeAsc(
      String symbol, Instant startTime, Instant endTime);

  @Query("SELECT DISTINCT r.symbol FROM RawKlineMirrorEntity r")
  List<String> findDistinctSymbols();
}
