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
public class UserBalanceData {
  private String userId;
  private String asset;
  private BigDecimal free;
  private BigDecimal locked;
  private Instant timestamp;
}
