package com.acodexm.dataloader.model;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeMarketData {
  private String symbol;
  private String window;
  private Instant startTime;
  private Instant endTime;
  private BigDecimal averagePrice;
}
