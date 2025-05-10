package com.acodexm.datatransformer.mapper;

import com.acodexm.datatransformer.entity.AggregatedKlineEntity;
import com.acodexm.datatransformer.entity.RawKlineMirrorEntity;
import com.acodexm.datatransformer.model.RawKlineData;
import com.acodexm.datatransformer.model.TimeMarketData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DataMapper {

  @Mapping(target = "id", ignore = true)
  RawKlineMirrorEntity toEntity(RawKlineData source);

  @Mapping(target = "symbol", source = "symbol")
  @Mapping(target = "window", source = "windowType")
  @Mapping(target = "startTime", source = "windowStartTime")
  @Mapping(target = "endTime", source = "windowEndTime")
  @Mapping(target = "averagePrice", source = "averagePrice")
  TimeMarketData toTimeMarketData(AggregatedKlineEntity source);
}
