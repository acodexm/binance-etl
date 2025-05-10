package com.acodexm.datatransformer.entity;

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
@Table(
    name = "raw_kline_mirror",
    uniqueConstraints = @UniqueConstraint(columnNames = {"symbol", "open_time"}))
public class RawKlineMirrorEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "symbol", nullable = false)
  private String symbol;

  @Column(name = "interval", nullable = false)
  private String interval;

  @Column(name = "open_time", nullable = false)
  private Instant openTime;

  @Column(name = "close_time")
  private Instant closeTime;

  @Column(name = "open_price", precision = 19, scale = 8)
  private BigDecimal open;

  @Column(name = "high_price", precision = 19, scale = 8)
  private BigDecimal high;

  @Column(name = "low_price", precision = 19, scale = 8)
  private BigDecimal low;

  @Column(name = "close_price", precision = 19, scale = 8)
  private BigDecimal close;

  @Column(name = "volume", precision = 19, scale = 8)
  private BigDecimal volume;

  @Column(name = "is_closed", nullable = false)
  private boolean isClosed;
}
