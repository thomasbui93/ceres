package ceres.crawler;

import ceres.exception.crawler.AuthorPageNotFoundException;
import ceres.exception.crawler.PoemLinksFetchingException;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import static com.ea.async.Async.await;

@Slf4j
public class PoemFetcher {
  private final String poetName;
  private final String baseUrl;

  public PoemFetcher(String baseUrl, String poetName) {
    this.poetName = poetName;
    this.baseUrl = baseUrl;
  }

  private CompletableFuture<String> getPoetPage() {
    try {
      String url = String.format("%s/qsearch.xml.php"
          + "?Core=author&Field=Name&Value=%s&Page=0", baseUrl, URLEncoder.encode(poetName));
      Document page = await(BaseCrawler.fetchPage(url));
      Elements links = page.select("a");
      var poetUrl = String.format("%s/%s", baseUrl, links.get(0).attr("href"));
      return CompletableFuture.completedFuture(poetUrl);
    } catch (IOException ex) {
      log.error(String.format("Failed to search for author: %s", poetName), ex.fillInStackTrace());
      return CompletableFuture.failedFuture(new AuthorPageNotFoundException());
    }
  }

  private CompletableFuture<List<String>> getPoemLinks() {
    try {
      String poetUrl = await(this.getPoetPage());
      Document page = await(BaseCrawler.fetchPage(poetUrl));
      Elements elements = page.select(".poem-group-list li a");
      var links = elements.stream()
          .map(el -> String.format("%s/%s", this.baseUrl, el.attr("href")))
          .collect(Collectors.toList());
      return CompletableFuture.completedFuture(links);
    } catch (IOException ex) {
      log.error(String.format("Failed to get links for author poems: %s.", poetName), ex.fillInStackTrace());
      return CompletableFuture.failedFuture(new PoemLinksFetchingException());
    }
  }

  public CompletableFuture<Void> fetchPoems() {
    List<String> links = await(getPoemLinks());
    return CompletableFuture.allOf(
        links.stream().map(this::getPoem).toArray(CompletableFuture[]::new));
  }

  private CompletableFuture<Poem> getPoem(String link) {
    try {
      Document page = await(BaseCrawler.fetchPage(link));
      Elements poemTitlesEls = page.select(".poem-view-separated > h4");
      Elements poemContentEls = page.select(".poem-view-separated > p");
      String pageTitle = page.select(".page-header h1").html();
      List<String> poemContents = poemContentEls
          .stream()
          .filter(el -> el.text().isEmpty())
          .map(el -> el.html().replace("<br>", "\n"))
          .collect(Collectors.toList());
      List<PoemContent> poems = new ArrayList<>();
      var index = 0;
      for (Element title : poemTitlesEls) {
        var poemContent = poemContents.get(index);
        var poem = PoemContent.builder()
            .title(title.text())
            .content(poemContent)
            .build();
        poems.add(poem);
        index ++;
      }
      return CompletableFuture.completedFuture(Poem.builder()
          .title(pageTitle)
          .content(poems)
          .build());
    } catch (IOException ex) {
      log.error(String.format("Failed to parse poem from a given link: %s.", link), ex.fillInStackTrace());
      return CompletableFuture.failedFuture(ex);
    }
  }
}
