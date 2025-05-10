package com.acodexm.dataloader.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

public interface PortfolioService {
    @Transactional
    void calculatePortfolioValue(String userId);

    @Transactional
    void recalculateAllPortfolios();

    @Scheduled(fixedRateString = "${portfolio.recalculation.interval:600000}")
    @Transactional
    void scheduledPortfolioUpdate();

    @Transactional(readOnly = true)
    Map<String, Object> getPortfolioSummary(String userId);
}
