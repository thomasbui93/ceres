package com.kbui.ceres.service.crawler.image;

import com.kbui.ceres.service.crawler.image.ghibli.GhibliCrawler;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ImageFetcher extends ImageCrawlerAbstract {
  @Value("${crawler.image.ghibli.baseUrl}")
  private String ghibliUrl;

  protected Future<List<List<ImageResult>>> getImages() {
    List<ImageCrawler> services = List.of(new GhibliCrawler(ghibliUrl));
    var futures = Arrays.asList(services.stream().map(ImageCrawler::fetchImages).toArray(Future[]::new));

    return Future.future((future) -> CompositeFuture
      .all(futures)
      .map(r -> new ArrayList<>(r.list()))
    );
  }
}
