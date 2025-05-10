package com.acodexm.binanceconnector.domain;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
  private String userId;
  private boolean active;
  private Instant createdAt;
  private Instant lastUpdated;
}
