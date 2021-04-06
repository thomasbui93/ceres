package com.kbui.ceres.service.crawler.image.ghibli;

import com.kbui.ceres.service.crawler.image.ImageCrawler;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;

public class GhibliCrawler implements ImageCrawler {

  @Autowired
  private GhibliCollectionCrawler collectionCrawler;

  @Override
  public Future<Boolean> fetchImages(String baseUrl) {
    return collectionCrawler
      .fetch()
      .compose(movieLinks -> {
        var images = movieLinks
          .stream()
          .map(GhibliMovieCrawler::fetch);

        var futures =
          Arrays.asList(images.toArray(Future[]::new));

        return  Future.future((future) -> CompositeFuture
          .all(futures)
          .onComplete(r -> future.complete(r.succeeded()))
        );
      });
  }
}
