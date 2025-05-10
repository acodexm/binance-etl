package com.acodexm.binanceconnector.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
    name = "user_balance",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "asset", "timestamp"})})
public class UserBalanceEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private String userId;

  @Column(nullable = false)
  private String asset;

  @Column(nullable = false)
  private BigDecimal free;

  @Column(nullable = false)
  private BigDecimal locked;

  @Column(nullable = false)
  private Instant timestamp;
}
