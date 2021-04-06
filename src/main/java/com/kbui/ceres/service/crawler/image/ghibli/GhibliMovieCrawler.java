package com.kbui.ceres.service.crawler.image.ghibli;

import com.kbui.ceres.service.crawler.BaseCrawler;
import io.vertx.core.Future;
import java.util.List;
import java.util.stream.Collectors;
public class GhibliMovieCrawler {
  private static final String selector = "img.panel-img-top";

  public static Future<List<GhibliImage>> fetch(String baseUrl) {
    return BaseCrawler.fetchPage(baseUrl)
      .map(page -> {
        var movie = page.selectFirst("h1").text();
        return page
          .select(selector)
          .stream()
          .map(el -> {
            var image = el.attr("src").replace("thumb-","").replace(".png", ".jpg");
            return GhibliImage
              .builder()
              .movie(movie)
              .imageUrl(image)
              .build();
          })
          .collect(Collectors.toList());
      });
  }
}
