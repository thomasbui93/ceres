package com.kbui.ceres.service.crawler.image.ghibli;

import com.kbui.ceres.service.crawler.BaseCrawler;
import io.vertx.core.Future;
import java.util.List;
import java.util.stream.Collectors;

public class GhibliCollectionCrawler {

  static final String linkSelector = "a.panelarea";

  public static Future<List<String>> fetch(String baseUrl) {
    return BaseCrawler.fetchPage(baseUrl)
        .map(
            page ->
                page.select(linkSelector).stream()
                    .map(el -> el.attr("href"))
                    .collect(Collectors.toList()));
  }
}
