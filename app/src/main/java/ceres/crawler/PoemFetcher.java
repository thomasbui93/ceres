package ceres.crawler;

import io.vertx.core.Future;

public interface PoemFetcher {
  public Future<Boolean> fetchPoems();
}
