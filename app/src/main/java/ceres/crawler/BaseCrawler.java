package ceres.crawler;

import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class BaseCrawler {
  private final String baseUrl;
  private Document page;

  public static Document fetchPage(String baseUrl) throws IOException {
    var crawler = new BaseCrawler(baseUrl);
    return crawler.getPage();
  }

  public BaseCrawler(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public Document getPage() throws IOException {
    this.page = this.page == null ? Jsoup.connect(this.baseUrl).get() : this.page;
    return this.page;
  }
}
