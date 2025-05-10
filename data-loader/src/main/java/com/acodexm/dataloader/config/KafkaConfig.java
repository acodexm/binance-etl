package com.acodexm.dataloader.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Data
@EnableKafka
@Configuration
@ConfigurationProperties(prefix = "kafka.topics")
public class KafkaConfig {

  private String rawMarketData = "raw_market_data";
  private String userBalances = "user_balance_data";
  private String userData = "user_data";
}
