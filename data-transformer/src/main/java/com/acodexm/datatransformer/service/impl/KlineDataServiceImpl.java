package com.acodexm.datatransformer.service.impl;

import com.acodexm.datatransformer.entity.RawKlineMirrorEntity;
import com.acodexm.datatransformer.mapper.DataMapper;
import com.acodexm.datatransformer.model.RawKlineData;
import com.acodexm.datatransformer.repository.RawKlineMirrorRepository;
import com.acodexm.datatransformer.service.KlineDataService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class KlineDataServiceImpl implements KlineDataService {

  private final RawKlineMirrorRepository repository;
  private final DataMapper dataMapper;

  @Override
  @Transactional
  public RawKlineMirrorEntity saveRawKlineData(@Valid @NotNull RawKlineData rawKlineData) {
    RawKlineMirrorEntity entity = dataMapper.toEntity(rawKlineData);
    return repository.save(entity);
  }

  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "klineDataCache", key = "{#symbol, #startTime, #endTime}")
  public List<RawKlineMirrorEntity> findBySymbolAndTimeRange(
      String symbol, Instant startTime, Instant endTime) {
    return repository.findBySymbolAndOpenTimeBetweenOrderByOpenTimeAsc(symbol, startTime, endTime);
  }

  @Override
  @Transactional(readOnly = true)
  @Cacheable(value = "symbolsCache")
  public List<String> getAllSymbols() {
    return repository.findDistinctSymbols();
  }
}
