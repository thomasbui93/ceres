package com.kbui.ceres.service.crawler.image;

import io.vertx.core.Future;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ImageCrawlerAbstract implements ImageCrawler {

  @Override
  public Future<List<ImageResult>> fetchImages() {
    return getImages().map(r -> r.stream()
      .flatMap(List::stream)
      .collect(Collectors.toList()));
  }

  protected abstract Future<List<List<ImageResult>>> getImages();
}
