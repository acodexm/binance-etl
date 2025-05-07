# Microservices Application with Kafka and Databases

This project consists of three Spring Boot applications:
- **binance-connector**: Connects to Binance API and publishes raw market data to Kafka
- **data-transformer**: Consumes raw market data, transforms it, and publishes it back to Kafka
- **data-loader**: Consumes transformed market data and stores it in TimescaleDB

## System Architecture

Each microservice has its own dedicated database:
- **binance-connector**: PostgreSQL database for storing connection metadata
- **data-transformer**: PostgreSQL database for transformation states and configurations
- **data-loader**: TimescaleDB (PostgreSQL extension) for time-series market data

Kafka is used as the messaging backbone between services.

## Running the Application

### Running the Complete Stack

To run the complete application stack with all services:
