package ceres.crawler;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class BaseCrawler {
  private final String baseUrl;
  private CompletableFuture<Document> page;

  public static CompletableFuture<Document> fetchPage(String baseUrl) throws IOException {
    var crawler = new BaseCrawler(baseUrl);
    return crawler.getPage();
  }

  public BaseCrawler(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public CompletableFuture<Document> getPage() {
    if (this.page == null) {
      this.page = CompletableFuture.supplyAsync(() -> {
        try {
          return Jsoup.connect(this.baseUrl).get();
        } catch (IOException ex) {
          return null;
        }
      });
    }
    return this.page;
  }
}
