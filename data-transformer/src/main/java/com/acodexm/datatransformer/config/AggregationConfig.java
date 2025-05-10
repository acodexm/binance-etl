package com.acodexm.datatransformer.config;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "aggregation")
public class AggregationConfig {
  private List<String> windows;
  private String schedule;
}
