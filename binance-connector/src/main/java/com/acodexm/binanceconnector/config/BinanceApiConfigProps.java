package com.acodexm.binanceconnector.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "binance.api")
public class BinanceApiConfigProps {
  private String key;
  private String secret;
  private String[] symbols = {"BTCUSDT", "ETHUSDT"};
  private String interval = "1m";
  private int fetchIntervalSeconds = 60;
  private boolean fetchUserData = true;
}
