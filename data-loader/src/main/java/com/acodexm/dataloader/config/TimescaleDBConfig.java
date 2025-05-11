package com.acodexm.dataloader.config;

import jakarta.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TimescaleDBConfig {

  private final JdbcTemplate jdbcTemplate;

  @Value("${timescale.enabled:true}")
  private boolean timescaleEnabled;

  @Value("${timescale.schema-init-timeout:30s}")
  private Duration initTimeout;

  @PostConstruct
  public void initTimescaleDB() {
    if (!timescaleEnabled) {
      log.info("TimescaleDB extension is disabled");
      return;
    }

    log.info("Initializing TimescaleDB extension");
    Instant startTime = Instant.now();

    try {
      // First ensure the extension is created
      createTimescaleExtension();

      // Create hypertables
      createHypertable("user_balances_view", "record_timestamp");
      createHypertable("symbol_time_window_prices", "window_start_time");
      createHypertable("symbol_performance_view", "window_start_time");
      createHypertable("portfolio_value_view", "timestamp");

      log.info(
          "TimescaleDB hypertables initialized successfully in {} ms",
          Duration.between(startTime, Instant.now()).toMillis());
    } catch (Exception e) {
      log.error("Failed to initialize TimescaleDB hypertables: {}", e.getMessage(), e);
      if (Duration.between(startTime, Instant.now()).compareTo(initTimeout) > 0) {
        log.error(
            "TimescaleDB initialization timed out after {} ms",
            Duration.between(startTime, Instant.now()).toMillis());
      }
    }
  }

  private void createTimescaleExtension() {
    try {
      // Check if the extension already exists
      boolean extensionExists =
          jdbcTemplate.queryForObject(
              "SELECT EXISTS (SELECT FROM pg_extension WHERE extname = 'timescaledb')",
              Boolean.class);

      if (extensionExists) {
        log.info("TimescaleDB extension already exists");
        return;
      }

      // Create the extension
      jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS timescaledb CASCADE");
      log.info("Created TimescaleDB extension");
    } catch (Exception e) {
      log.error("Error creating TimescaleDB extension: {}", e.getMessage(), e);
      throw e;
    }
  }

  private void createHypertable(String tableName, String timeColumn) {
    try {
      // Check if table exists
      boolean tableExists =
          jdbcTemplate.queryForObject(
              "SELECT EXISTS (SELECT FROM information_schema.tables WHERE table_name = ?)",
              Boolean.class,
              tableName);

      if (!tableExists) {
        log.warn("Table '{}' does not exist yet, skipping hypertable creation", tableName);
        return;
      }

      // Check if the table is already a hypertable
      boolean isHypertable =
          jdbcTemplate.queryForObject(
              "SELECT EXISTS (SELECT FROM timescaledb_information.hypertables WHERE hypertable_name = ?)",
              Boolean.class,
              tableName);

      if (isHypertable) {
        log.info("Table '{}' is already a hypertable", tableName);
        return;
      }

      // Create hypertable
      jdbcTemplate.execute(
          "SELECT create_hypertable('"
              + tableName
              + "', '"
              + timeColumn
              + "', if_not_exists => TRUE)");
      log.info("Created hypertable '{}' with time column '{}'", tableName, timeColumn);
    } catch (Exception e) {
      log.error("Error creating hypertable for table '{}': {}", tableName, e.getMessage(), e);
      throw e;
    }
  }
}
