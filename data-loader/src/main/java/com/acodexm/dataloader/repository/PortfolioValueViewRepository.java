package com.acodexm.dataloader.repository;

import com.acodexm.dataloader.entity.PortfolioValueViewEntity;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PortfolioValueViewRepository
    extends JpaRepository<PortfolioValueViewEntity, Long> {

  Optional<PortfolioValueViewEntity> findFirstByUserIdOrderByTimestampDesc(String userId);

  List<PortfolioValueViewEntity> findByUserIdAndTimestampBetweenOrderByTimestampAsc(
      String userId, Instant startTime, Instant endTime);

  List<PortfolioValueViewEntity> findByUserIdOrderByTimestampDesc(String userId);
}
