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
@Table(name = "user_balances_view")
public class UserBalanceViewEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id", nullable = false)
  private String userId;

  @Column(name = "asset", nullable = false)
  private String asset;

  @Column(name = "free", precision = 19, scale = 8, nullable = false)
  private BigDecimal free;

  @Column(name = "locked", precision = 19, scale = 8, nullable = false)
  private BigDecimal locked;

  @Column(name = "record_timestamp", nullable = false)
  private Instant recordTimestamp;

  @Column(name = "total_amount", precision = 19, scale = 8, nullable = false)
  private BigDecimal totalAmount;
}
