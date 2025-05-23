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
public class UserBalance {
  private String userId;
  private String asset;
  private BigDecimal free;
  private BigDecimal locked;

  @Builder.Default private Instant timestamp = Instant.now();
}
