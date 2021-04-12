package com.kbui.ceres.service.crawler.image;

import io.vertx.core.Future;
import java.util.List;

public interface ImageCrawler {
  Future<List<ImageResult>> fetchImages();
}
