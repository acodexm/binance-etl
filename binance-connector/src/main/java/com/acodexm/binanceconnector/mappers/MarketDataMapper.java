package com.acodexm.binanceconnector.mappers;

import com.acodexm.binanceconnector.domain.KlineData;
import com.acodexm.binanceconnector.domain.UserBalance;
import com.acodexm.binanceconnector.entities.KlineDataEntity;
import com.acodexm.binanceconnector.entities.UserBalanceEntity;
import java.time.Instant;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = Instant.class)
public interface MarketDataMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "timestamp", expression = "java(Instant.now())")
  UserBalanceEntity toEntity(UserBalance userBalance);

  UserBalance toDomain(UserBalanceEntity userBalanceEntity);

  List<UserBalance> toDomainList(List<UserBalanceEntity> userBalanceEntities);

  @Mapping(target = "id", ignore = true)
  KlineDataEntity toEntity(KlineData klineData);

  KlineData toDomain(KlineDataEntity klineDataEntity);

  List<KlineData> toKlineDataList(List<KlineDataEntity> klineDataEntities);
}
