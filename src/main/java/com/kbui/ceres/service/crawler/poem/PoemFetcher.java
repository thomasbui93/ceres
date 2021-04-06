package com.kbui.ceres.service.crawler.poem;

import io.vertx.core.Future;

public interface PoemFetcher {
  Future<Boolean> fetchPoems(String poetName, String baseUrl);
}
