package com.acodexm.dataloader.model;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserData {
  private String userId;
  private boolean active;
  private Instant createdAt;
  private Instant lastUpdated;
}
