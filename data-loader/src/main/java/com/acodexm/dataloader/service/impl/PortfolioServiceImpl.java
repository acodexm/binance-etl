package com.acodexm.dataloader.service.impl;

import com.acodexm.dataloader.entity.PortfolioValueViewEntity;
import com.acodexm.dataloader.entity.SymbolTimeWindowPriceEntity;
import com.acodexm.dataloader.entity.UserBalanceViewEntity;
import com.acodexm.dataloader.repository.PortfolioValueViewRepository;
import com.acodexm.dataloader.repository.SymbolTimeWindowPriceRepository;
import com.acodexm.dataloader.repository.UserBalanceViewRepository;
import com.acodexm.dataloader.repository.UserRepository;
import com.acodexm.dataloader.service.PortfolioService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {

  private final UserBalanceViewRepository userBalanceRepository;
  private final SymbolTimeWindowPriceRepository timeWindowPriceRepository;
  private final PortfolioValueViewRepository portfolioValueRepository;
  private final UserRepository userRepository;

  /** Calculate and save the portfolio value for a specific user */
  @Transactional
  @Override
  public void calculatePortfolioValue(String userId) {
    log.debug("Calculating portfolio value for user: {}", userId);

    // Verify user exists
    if (userRepository.findByUserId(userId).isEmpty()) {
      log.warn("User with ID {} not found, cannot calculate portfolio", userId);
      return;
    }

    // Get latest balances for all assets of the user
    List<UserBalanceViewEntity> latestBalances =
        userBalanceRepository.findLatestBalancesByUserId(userId);

    if (latestBalances.isEmpty()) {
      log.warn("No balance data found for user: {}", userId);
      return;
    }

    BigDecimal totalValue = BigDecimal.ZERO;
    int assetCount = 0;

    for (UserBalanceViewEntity balance : latestBalances) {
      // Skip assets with zero balance
      if (balance.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
        continue;
      }

      // For each asset, get the latest price (use 1h window for current price)
      String symbol = balance.getAsset() + "USDT"; // Assume USDT as quote currency
      Optional<SymbolTimeWindowPriceEntity> latestPrice =
          timeWindowPriceRepository.findFirstBySymbolAndWindowTypeOrderByWindowStartTimeDesc(
              symbol, "1h");

      if (latestPrice.isPresent()) {
        BigDecimal price = latestPrice.get().getAveragePrice();
        BigDecimal assetValue = balance.getTotalAmount().multiply(price);
        totalValue = totalValue.add(assetValue);
        assetCount++;

        log.debug(
            "Asset: {}, Amount: {}, Price: {}, Value: {}",
            balance.getAsset(),
            balance.getTotalAmount(),
            price,
            assetValue);
      } else {
        log.warn("No price data found for symbol: {}", symbol);
      }
    }

    // Save portfolio value
    PortfolioValueViewEntity portfolioValue =
        PortfolioValueViewEntity.builder()
            .userId(userId)
            .totalValue(totalValue)
            .assetCount(assetCount)
            .timestamp(Instant.now())
            .baseCurrency("USDT")
            .build();

    portfolioValueRepository.save(portfolioValue);
    log.info(
        "Saved portfolio value for user: {}, total value: {} USDT, assets: {}",
        userId,
        totalValue.setScale(2, RoundingMode.HALF_UP),
        assetCount);
  }

  /** Recalculate portfolio value for all users */
  @Transactional
  @Override
  public void recalculateAllPortfolios() {
    log.debug("Recalculating portfolio values for all users");

    // Get all active users and calculate their portfolios
    userRepository.findAll().stream()
        .filter(user -> user.isActive())
        .map(user -> user.getUserId())
        .forEach(this::calculatePortfolioValue);
  }

  /** Scheduled job to update portfolio values */
  @Scheduled(fixedRateString = "${portfolio.recalculation.interval:600000}")
  @Transactional
  @Override
  public void scheduledPortfolioUpdate() {
    log.debug("Running scheduled portfolio value update");
    recalculateAllPortfolios();
  }

  /** Get user's portfolio summary with asset details and values */
  @Transactional(readOnly = true)
  @Override
  public Map<String, Object> getPortfolioSummary(String userId) {
    Map<String, Object> summary = new HashMap<>();

    // Get latest portfolio value
    Optional<PortfolioValueViewEntity> latestValue =
        portfolioValueRepository.findFirstByUserIdOrderByTimestampDesc(userId);

    if (latestValue.isPresent()) {
      PortfolioValueViewEntity portfolio = latestValue.get();
      summary.put("totalValue", portfolio.getTotalValue());
      summary.put("assetCount", portfolio.getAssetCount());
      summary.put("timestamp", portfolio.getTimestamp());
      summary.put("baseCurrency", portfolio.getBaseCurrency());
    } else {
      summary.put("totalValue", BigDecimal.ZERO);
      summary.put("assetCount", 0);
      summary.put("timestamp", Instant.now());
      summary.put("baseCurrency", "USDT");
    }

    // Get latest balances for all assets
    List<UserBalanceViewEntity> latestBalances =
        userBalanceRepository.findLatestBalancesByUserId(userId);

    Map<String, Map<String, Object>> assets = new HashMap<>();

    for (UserBalanceViewEntity balance : latestBalances) {
      // Skip assets with zero balance
      if (balance.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
        continue;
      }

      Map<String, Object> assetDetails = new HashMap<>();
      assetDetails.put("free", balance.getFree());
      assetDetails.put("locked", balance.getLocked());
      assetDetails.put("total", balance.getTotalAmount());

      // Get current price for asset
      String symbol = balance.getAsset() + "USDT";
      Optional<SymbolTimeWindowPriceEntity> latestPrice =
          timeWindowPriceRepository.findFirstBySymbolAndWindowTypeOrderByWindowStartTimeDesc(
              symbol, "1h");

      if (latestPrice.isPresent()) {
        BigDecimal price = latestPrice.get().getAveragePrice();
        BigDecimal value = balance.getTotalAmount().multiply(price);

        assetDetails.put("price", price);
        assetDetails.put("value", value);
      } else {
        assetDetails.put("price", null);
        assetDetails.put("value", null);
      }

      assets.put(balance.getAsset(), assetDetails);
    }

    summary.put("assets", assets);
    return summary;
  }
}
