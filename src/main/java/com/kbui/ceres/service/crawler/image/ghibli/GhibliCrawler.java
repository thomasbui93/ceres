package com.kbui.ceres.service.crawler.image.ghibli;

import com.kbui.ceres.service.crawler.image.ImageCrawlerAbstract;
import com.kbui.ceres.service.crawler.image.ImageResult;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GhibliCrawler extends ImageCrawlerAbstract {
  private final String baseUrl;

  public GhibliCrawler(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  protected Future<List<List<ImageResult>>> getImages() {
    return GhibliCollectionCrawler
      .fetch(this.baseUrl)
      .compose(movieLinks -> {
        var images = movieLinks
          .stream()
          .map(GhibliMovieCrawler::fetch);

        var futures = Arrays.asList(images.toArray(Future[]::new));

        return Future.future((future) -> CompositeFuture
          .all(futures)
          .map(r -> new ArrayList<>(r.list()))
        );
      });
  }
}
