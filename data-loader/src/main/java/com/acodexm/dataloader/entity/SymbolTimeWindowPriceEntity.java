package com.acodexm.dataloader.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "symbol_time_window_prices")
public class SymbolTimeWindowPriceEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "symbol", nullable = false)
  private String symbol;

  @Column(name = "window_type", nullable = false)
  private String windowType;

  @Column(name = "window_start_time", nullable = false)
  private Instant windowStartTime;

  @Column(name = "window_end_time", nullable = false)
  private Instant windowEndTime;

  @Column(name = "average_price", precision = 19, scale = 8, nullable = false)
  private BigDecimal averagePrice;

  @Column(name = "record_timestamp", nullable = false)
  private Instant recordTimestamp;
}
