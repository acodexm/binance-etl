package com.acodexm.datatransformer.model;

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
public class RawKlineData {
  private String symbol;
  private String interval;
  private Instant openTime;
  private BigDecimal open;
  private BigDecimal high;
  private BigDecimal low;
  private BigDecimal close;
  private BigDecimal volume;
  private Instant closeTime;
  private BigDecimal quoteAssetVolume;
  private Long numberOfTrades;
  private BigDecimal takerBuyBaseAssetVolume;
  private BigDecimal takerBuyQuoteAssetVolume;
  private boolean isClosed;
}
