package com.acodexm.datatransformer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka.topics")
public class KafkaTopicConfig {

  private String rawKlineData;
  private String timeMarketData;
}
