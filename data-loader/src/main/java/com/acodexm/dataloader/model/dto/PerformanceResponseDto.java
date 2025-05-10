package com.acodexm.dataloader.model.dto;

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
public class PerformanceResponseDto {
  private String symbol;
  private String windowType;
  private Instant windowStartTime;
  private Instant windowEndTime;
  private BigDecimal averagePrice;
  private BigDecimal previousAveragePrice;
  private BigDecimal gainLossPercent;
  private Instant timestamp;
}
