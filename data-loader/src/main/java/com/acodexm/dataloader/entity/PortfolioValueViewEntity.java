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
@Table(name = "portfolio_value_view")
public class PortfolioValueViewEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private String userId;

  @Column(name = "total_value", precision = 19, scale = 8, nullable = false)
  private BigDecimal totalValue;

  @Column(name = "asset_count", nullable = false)
  private Integer assetCount;

  @Column(name = "timestamp", nullable = false)
  private Instant timestamp;

  @Column(name = "base_currency", nullable = false)
  private String baseCurrency = "USDT";
}
