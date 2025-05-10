package com.acodexm.binanceconnector.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "users")
public class UserEntity {

  @Id
  @Column(name = "user_id")
  private String userId;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "active", nullable = false)
  private boolean active;

  @Column(name = "last_updated", nullable = false)
  private Instant lastUpdated;
}
