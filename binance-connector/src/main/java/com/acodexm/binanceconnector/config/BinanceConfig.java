package com.acodexm.binanceconnector.config;

import com.binance.connector.client.common.configuration.ClientConfiguration;
import com.binance.connector.client.common.configuration.SignatureConfiguration;
import com.binance.connector.client.spot.rest.SpotRestApiUtil;
import com.binance.connector.client.spot.rest.api.SpotRestApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class BinanceConfig {

  private final BinanceApiConfigProps configProps;

  @Bean
  public SpotRestApi spotRestClient() {
    ClientConfiguration clientConfiguration = SpotRestApiUtil.getClientConfiguration();

    // Configure API key and secret for authenticated endpoints
    if (StringUtils.hasText(configProps.getKey()) && StringUtils.hasText(configProps.getSecret())) {
      log.info("Configuring Binance API with authentication");
      SignatureConfiguration signatureConfiguration = new SignatureConfiguration();
      signatureConfiguration.setApiKey(configProps.getKey());
      signatureConfiguration.setSecretKey(configProps.getSecret());
      clientConfiguration.setSignatureConfiguration(signatureConfiguration);
    } else {
      log.warn("Binance API key or secret not provided. Only public endpoints will be available.");
    }

    // Add reasonable timeouts
    clientConfiguration.setReadTimeout(30000); // 30 seconds
    clientConfiguration.setConnectTimeout(10000); // 10 seconds

    return new SpotRestApi(clientConfiguration);
  }
}
