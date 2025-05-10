package com.acodexm.binanceconnector.services.impl;

import com.acodexm.binanceconnector.config.BinanceApiConfigProps;
import com.acodexm.binanceconnector.domain.KlineData;
import com.acodexm.binanceconnector.domain.UserBalance;
import com.acodexm.binanceconnector.services.BinanceApiService;
import com.binance.connector.client.common.ApiResponse;
import com.binance.connector.client.spot.rest.api.SpotRestApi;
import com.binance.connector.client.spot.rest.model.GetAccountResponse;
import com.binance.connector.client.spot.rest.model.Interval;
import com.binance.connector.client.spot.rest.model.KlinesResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BinanceApiServiceImpl implements BinanceApiService {

  private final SpotRestApi spotRestApi;
  private final BinanceApiConfigProps configProps;
  private final ObjectMapper objectMapper;

  @Override
  public List<KlineData> fetchKlineData(Set<String> symbols, String i) {
    return symbols.stream()
        .flatMap(
            symbol -> {
              try {
                Interval interval = Interval.fromValue(i);
                Long endTime = Instant.now().toEpochMilli();
                Long startTime = endTime - (24 * 60 * 60 * 1000); // 24 hours ago
                String timeZone = null;
                Integer limit = 5;
                ApiResponse<KlinesResponse> response =
                    spotRestApi.klines(symbol, interval, startTime, endTime, timeZone, limit);
                log.debug("Fetched {} kline data for symbol {}", response.getData(), symbol);

                return response.getData().stream()
                    .map(
                        klineNode ->
                            KlineData.builder()
                                .symbol(symbol)
                                .interval(i)
                                .openTime(
                                    Instant.ofEpochMilli(Long.parseLong(klineNode.getFirst())))
                                .open(new BigDecimal(klineNode.get(1)))
                                .high(new BigDecimal(klineNode.get(2)))
                                .low(new BigDecimal(klineNode.get(3)))
                                .close(new BigDecimal(klineNode.get(4)))
                                .volume(new BigDecimal(klineNode.get(5)))
                                .closeTime(Instant.ofEpochMilli(Long.parseLong(klineNode.get(6))))
                                .quoteAssetVolume(new BigDecimal(klineNode.get(7)))
                                .numberOfTrades(Long.valueOf(klineNode.get(8)))
                                .takerBuyBaseAssetVolume(new BigDecimal(klineNode.get(9)))
                                .takerBuyQuoteAssetVolume(new BigDecimal(klineNode.get(10)))
                                .isClosed(true) // Assuming historical data is closed
                                .build());
              } catch (Exception e) {
                log.error("Error fetching kline data for symbol {}: {}", symbol, e.getMessage());
                return Stream.empty();
              }
            })
        .toList();
  }

  @Override
  public List<UserBalance> fetchUserBalances() {
    if (!configProps.isFetchUserData()) {
      log.warn("User data fetching is disabled in configuration");
      return Collections.emptyList();
    }

    try {
      Boolean omitZeroBalances = true;
      Long recvWindow = 5000L;
      ApiResponse<GetAccountResponse> response =
          spotRestApi.getAccount(omitZeroBalances, recvWindow);
      log.debug("Fetched user balances: {}", response.getData());
      JsonNode root = objectMapper.readTree(response.getData().toJson());
      JsonNode balancesNode = root.get("balances");

      List<UserBalance> balances = new ArrayList<>();

      if (balancesNode != null && balancesNode.isArray()) {
        for (JsonNode balanceNode : balancesNode) {
          String asset = balanceNode.get("asset").asText();
          BigDecimal free = new BigDecimal(balanceNode.get("free").asText());
          BigDecimal locked = new BigDecimal(balanceNode.get("locked").asText());

          // Skip assets with zero balance
          if (free.compareTo(BigDecimal.ZERO) > 0 || locked.compareTo(BigDecimal.ZERO) > 0) {
            UserBalance balance =
                UserBalance.builder().asset(asset).free(free).locked(locked).build();
            balances.add(balance);
          }
        }
      }

      return balances;
    } catch (Exception e) {
      log.error("Error fetching user balances: {}", e.getMessage());
      return Collections.emptyList();
    }
  }

  @Override
  public List<KlineData> fetchKlineDataForPortfolio(List<UserBalance> balances, String interval) {
    // Extract asset symbols and convert to trading pairs with USDT
    Set<String> symbols =
        balances.stream()
            .map(UserBalance::getAsset)
            .filter(asset -> !asset.equals("USDT")) // Exclude USDT itself
            .map(asset -> asset + "USDT")
            .collect(Collectors.toSet());

    // If no assets found, use default symbols from config
    if (symbols.isEmpty()) {
      log.info("No assets found in portfolio, using default symbols from config");
      symbols = Set.of(configProps.getSymbols());
    }

    // Fetch kline data for the symbols
    return fetchKlineData(symbols, interval);
  }
}
