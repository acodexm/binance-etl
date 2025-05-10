package com.acodexm.datatransformer.service;

import com.acodexm.datatransformer.model.TimeMarketData;

public interface TimeMarketDataPublisherService {

  void publishTimeMarketData(TimeMarketData timeMarketData);
}
