package com.acodexm.binanceconnector.usecases;

import com.acodexm.binanceconnector.config.BinanceApiConfigProps;
import com.acodexm.binanceconnector.domain.KlineData;
import com.acodexm.binanceconnector.domain.UserBalance;
import com.acodexm.binanceconnector.entities.KlineDataEntity;
import com.acodexm.binanceconnector.entities.UserBalanceEntity;
import com.acodexm.binanceconnector.mappers.MarketDataMapper;
import com.acodexm.binanceconnector.repositories.KlineDataRepository;
import com.acodexm.binanceconnector.repositories.UserBalanceRepository;
import com.acodexm.binanceconnector.services.BinanceApiService;
import com.acodexm.binanceconnector.services.MarketDataPublisherService;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class MarketDataFetchUseCase {

  private final BinanceApiService binanceApiService;
  private final MarketDataPublisherService publisherService;
  private final UserBalanceRepository userBalanceRepository;
  private final KlineDataRepository klineDataRepository;
  private final MarketDataMapper marketDataMapper;
  private final BinanceApiConfigProps configProps;

  /**
   * Executes the market data fetching process: 1. Fetches user balances 2. Saves user balances to
   * database 3. Publishes user balances to Kafka 4. Fetches kline data for portfolio or configured
   * symbols 5. Saves kline data to database 6. Publishes kline data to Kafka
   */
  @Transactional
  public void execute() {
    log.info("Executing market data fetch use case");

    // Process for user balance data
    if (configProps.isFetchUserData()) {
      List<UserBalance> balances = fetchAndSaveUserBalances();
      List<KlineData> klineDataList = fetchKlineDataBasedOnUserBalances(balances);
      saveAndPublishKlineData(klineDataList);
    } else {
      Set<String> symbols = new HashSet<>(Arrays.asList(configProps.getSymbols()));
      List<KlineData> klineDataList =
          binanceApiService.fetchKlineData(symbols, configProps.getInterval());
      saveAndPublishKlineData(klineDataList);
    }
  }

  private List<UserBalance> fetchAndSaveUserBalances() {
    // Fetch user balances
    List<UserBalance> balances = binanceApiService.fetchUserBalances();
    log.info("Fetched {} user balances", balances.size());

    // Save to database
    List<UserBalanceEntity> entities =
        balances.stream().map(marketDataMapper::toEntity).collect(Collectors.toList());
    userBalanceRepository.saveAll(entities);
    log.info("Saved {} user balances to database", entities.size());

    // Publish to Kafka
    balances.forEach(publisherService::publishUserBalance);
    log.info("Published {} user balances to Kafka", balances.size());

    return balances;
  }

  private List<KlineData> fetchKlineDataBasedOnUserBalances(List<UserBalance> balances) {
    return binanceApiService.fetchKlineDataForPortfolio(balances, configProps.getInterval());
  }

  private void saveAndPublishKlineData(List<KlineData> klineDataList) {
    // Save to database
    List<KlineDataEntity> entities =
        klineDataList.stream().map(marketDataMapper::toEntity).collect(Collectors.toList());
    klineDataRepository.saveAll(entities);
    log.info("Saved {} kline data records to database", entities.size());

    // Publish to Kafka
    klineDataList.forEach(publisherService::publishKlineData);
    log.info("Published {} kline data records to Kafka", klineDataList.size());
  }
}
