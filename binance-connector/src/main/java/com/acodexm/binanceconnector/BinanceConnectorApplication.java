package com.acodexm.binanceconnector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BinanceConnectorApplication {

  public static void main(String[] args) {
    SpringApplication.run(BinanceConnectorApplication.class, args);
  }
}
