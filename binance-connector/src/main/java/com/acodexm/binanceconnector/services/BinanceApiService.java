package com.acodexm.binanceconnector.services;

import com.acodexm.binanceconnector.domain.KlineData;
import com.acodexm.binanceconnector.domain.User;
import com.acodexm.binanceconnector.domain.UserBalance;
import java.util.List;
import java.util.Set;

public interface BinanceApiService {

  /**
   * Fetches kline data for the specified symbols
   *
   * @param symbols the set of symbols to fetch
   * @param interval the kline interval
   * @return a list of kline data
   */
  List<KlineData> fetchKlineData(Set<String> symbols, String interval);

  /**
   * Fetches user account balances
   *
   * @return list of user balances for all assets
   */
  List<UserBalance> fetchUserBalances(User user);

  /**
   * Fetches kline data for symbols that exist in the user's portfolio
   *
   * @param balances list of user balances to determine portfolio assets
   * @param interval the kline interval
   * @return a list of kline data for portfolio assets
   */
  List<KlineData> fetchKlineDataForPortfolio(List<UserBalance> balances, String interval);

  /**
   * Fetches the user ID from the Binance API
   *
   * @return the user ID from the Binance account
   */
  User fetchUser();
}
