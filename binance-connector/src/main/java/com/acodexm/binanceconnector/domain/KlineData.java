package com.acodexm.binanceconnector.domain;

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
public class KlineData {
  private String symbol;
  private Instant openTime;
  private Instant closeTime;
  private String interval;
  private BigDecimal open;
  private BigDecimal high;
  private BigDecimal low;
  private BigDecimal close;
  private BigDecimal volume;
  private BigDecimal quoteAssetVolume;
  private Long numberOfTrades;
  private Boolean isClosed;
  private BigDecimal takerBuyBaseAssetVolume;
  private BigDecimal takerBuyQuoteAssetVolume;
}
