package com.kbui.ceres.crawler;

import io.vertx.core.Future;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class BaseCrawler {
  private final String baseUrl;
  private Document page;

  public static Future<Document> fetchPage(String baseUrl) {
    var crawler = new BaseCrawler(baseUrl);
    return crawler.getPage();
  }

  public BaseCrawler(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public Future<Document> getPage() {
    return Future.future(r -> {
      if (this.page == null) {
        try {
          this.page = Jsoup.connect(this.baseUrl).get();
          r.complete(this.page);
        } catch (IOException e) {
          this.page = null;
          r.fail(e);
        }
      }
    });
  }
}
