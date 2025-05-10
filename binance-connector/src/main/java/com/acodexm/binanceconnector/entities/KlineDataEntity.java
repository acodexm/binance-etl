package com.acodexm.binanceconnector.entities;

import jakarta.persistence.*;
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
@Entity
@Table(
    name = "kline_data",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"symbol", "open_time", "interval"})})
public class KlineDataEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String symbol;

  @Column(name = "open_time", nullable = false)
  private Instant openTime;

  @Column(name = "close_time", nullable = false)
  private Instant closeTime;

  @Column(nullable = false)
  private String interval;

  @Column(nullable = false, precision = 20, scale = 8)
  private BigDecimal open;

  @Column(nullable = false, precision = 20, scale = 8)
  private BigDecimal high;

  @Column(nullable = false, precision = 20, scale = 8)
  private BigDecimal low;

  @Column(nullable = false, precision = 20, scale = 8)
  private BigDecimal close;

  @Column(nullable = false, precision = 20, scale = 8)
  private BigDecimal volume;

  @Column(name = "quote_asset_volume", nullable = false, precision = 20, scale = 8)
  private BigDecimal quoteAssetVolume;

  @Column(name = "number_of_trades", nullable = false)
  private Long numberOfTrades;

  @Column(name = "is_closed", nullable = false)
  private Boolean isClosed;

  @Column(name = "taker_buy_base_asset_volume", nullable = false, precision = 20, scale = 8)
  private BigDecimal takerBuyBaseAssetVolume;

  @Column(name = "taker_buy_quote_asset_volume", nullable = false, precision = 20, scale = 8)
  private BigDecimal takerBuyQuoteAssetVolume;
}
