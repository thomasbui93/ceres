package com.kbui.ceres.service.crawler.image;

import io.vertx.core.Future;

public interface ImageCrawler {
  Future<Boolean> fetchImages(String baseUrl);
}
