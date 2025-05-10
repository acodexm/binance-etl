package com.acodexm.dataloader.model.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioSummaryDto {
  private String userId;
  private BigDecimal totalValue;
  private Integer assetCount;
  private String baseCurrency;
  private Instant timestamp;
  private Map<String, AssetDetailsDto> assets;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class AssetDetailsDto {
    private String asset;
    private BigDecimal free;
    private BigDecimal locked;
    private BigDecimal total;
    private BigDecimal price;
    private BigDecimal value;
  }
}
