package com.kbui.ceres.service.crawler;

import io.vertx.core.Future;

public interface PoemFetcher {
  public Future<Boolean> fetchPoems(String poetName, String baseUrl);
}
