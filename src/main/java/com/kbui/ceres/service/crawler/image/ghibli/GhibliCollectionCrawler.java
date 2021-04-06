package com.kbui.ceres.service.crawler.image.ghibli;

import com.kbui.ceres.service.crawler.BaseCrawler;
import io.vertx.core.Future;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class GhibliCollectionCrawler {
  @Value("${crawler.image.ghibli.baseUrl}")
  private String baseUrl;

  private final String linkSelector = "a.panelarea";

  public Future<List<String>> fetch() {
    return BaseCrawler.fetchPage(baseUrl)
        .map(
            page ->
                page.select(linkSelector).stream()
                    .map(el -> el.attr("href"))
                    .collect(Collectors.toList()));
  }
}
