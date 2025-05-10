package com.acodexm.binanceconnector.usecases;

import com.acodexm.binanceconnector.config.BinanceApiConfigProps;
import com.acodexm.binanceconnector.domain.KlineData;
import com.acodexm.binanceconnector.domain.User;
import com.acodexm.binanceconnector.domain.UserBalance;
import com.acodexm.binanceconnector.entities.KlineDataEntity;
import com.acodexm.binanceconnector.entities.UserBalanceEntity;
import com.acodexm.binanceconnector.mappers.MarketDataMapper;
import com.acodexm.binanceconnector.repositories.KlineDataRepository;
import com.acodexm.binanceconnector.repositories.UserBalanceRepository;
import com.acodexm.binanceconnector.services.BinanceApiService;
import com.acodexm.binanceconnector.services.MarketDataPublisherService;
import com.acodexm.binanceconnector.services.UserService;
import java.util.*;
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
  private final UserService userService;

  @Transactional
  public void execute() {
    log.info("Executing market data fetch use case");

    if (configProps.isFetchUserData()) {
      User user = userService.getCurrentUser();
      publisherService.publishUserData(user);
      List<UserBalance> balances = fetchAndSaveUserBalances(user);
      List<KlineData> klineDataList = fetchKlineDataBasedOnUserBalances(balances);
      saveAndPublishKlineData(klineDataList);
    } else {
      Set<String> symbols = new HashSet<>(Arrays.asList(configProps.getSymbols()));
      List<KlineData> klineDataList =
          binanceApiService.fetchKlineData(symbols, configProps.getInterval());
      saveAndPublishKlineData(klineDataList);
    }
  }

  private List<UserBalance> fetchAndSaveUserBalances(User user) {
    List<UserBalance> balances = binanceApiService.fetchUserBalances(user);
    log.info("Fetched {} user balances", balances.size());

    List<UserBalanceEntity> entities =
        balances.stream().map(marketDataMapper::toEntity).collect(Collectors.toList());
    userBalanceRepository.saveAll(entities);
    log.info("Saved {} user balances to database", entities.size());

    balances.forEach(publisherService::publishUserBalance);
    log.info("Published {} user balances to Kafka", balances.size());

    return balances;
  }

  private List<KlineData> fetchKlineDataBasedOnUserBalances(List<UserBalance> balances) {
    return binanceApiService.fetchKlineDataForPortfolio(balances, configProps.getInterval());
  }

  private void saveAndPublishKlineData(List<KlineData> klineDataList) {
    List<KlineDataEntity> entities =
        klineDataList.stream().map(marketDataMapper::toEntity).collect(Collectors.toList());
    klineDataRepository.saveAll(entities);
    log.info("Saved {} kline data records to database", entities.size());

    klineDataList.forEach(publisherService::publishKlineData);
    log.info("Published {} kline data records to Kafka", klineDataList.size());
  }
}
