package com.acodexm.binanceconnector.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Data
@EnableKafka
@Configuration
@ConfigurationProperties(prefix = "kafka.topics")
public class KafkaConfig {

  private String klineData = "raw_kline_data";
  private String userBalance = "user_balance_data";
}
