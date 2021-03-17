package ceres.crawler;

import ceres.configuration.PoemConfiguration;
import ceres.repository.PoemRepository;
import ceres.repository.PoetRepository;
import ceres.repository.models.Poet;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@Slf4j
public class PoemFetcher {
  private final String poetName;
  private final String baseUrl;
  private Poet poet;

  @Inject
  private final PoetRepository poetRepository;
  @Inject
  private final PoemRepository poemRepository;

  public PoemFetcher(PoemConfiguration poemConfiguration,
      PoetRepository poetRepository, PoemRepository poemRepository) {
    this.poetName = poemConfiguration.getAuthors().get(0);
    this.baseUrl = poemConfiguration.getBaseUrl();
    this.poetRepository = poetRepository;
    this.poemRepository = poemRepository;
  }

  private Future<String> getPoetPage() {
    return this.poetRepository.get(poetName)
        .flatMap(poet -> {
          this.poet = poet;
          if (poet == null) {
            return fetchPoetPage()
                .flatMap(poetPage -> this.poetRepository.save(Poet
                    .builder()
                    .poetUrl(poetPage)
                    .poetName(poetName)
                    .build()
                ))
                .map(Poet::getPoetUrl);
          }
          return Future.succeededFuture(poet.getPoetUrl());
        });
  }

  private Future<String> fetchPoetPage() {
    String url =
        String.format(
            "%s/qsearch.xml.php" + "?Core=author&Field=Name&Value=%s&Page=0",
            baseUrl, URLEncoder.encode(poetName));
    return BaseCrawler.fetchPage(url)
        .map(page -> page.select("a"))
        .map(links -> String.format("%s/%s", baseUrl, links.get(0).attr("href")));
  }

  private Future<List<String>> getPoemLinks(String poetUrl) {
    return BaseCrawler.fetchPage(poetUrl)
        .map(page -> {
          var els = page.select(".poem-group-list li a");
          return els.stream()
              .map(el -> String.format("%s/%s", this.baseUrl, el.attr("href")))
              .collect(Collectors.toList());
        });
  }

  public Future<Boolean> fetchPoems() {
    return getPoetPage()
        .flatMap(this::getPoemLinks)
        .flatMap(links -> {
          var futures = Arrays.asList(
              links.stream().map(this::getAndSavePoem).toArray(Future[]::new));
          return Future.future((future) -> CompositeFuture.all(futures)
              .onComplete(r -> future.complete(r.succeeded())));
        });
  }

  private Future<ceres.repository.models.Poem> getAndSavePoem(String link) {
    return getPoem(link).compose(poem -> poemRepository.save(ceres.repository.models.Poem
        .builder()
        .name(poem.getTitle())
        .url(link)
        .content(poem.getContent())
        .author(poet)
        .build())
    );
  }

  private Future<Poem> getPoem(String link) {
    return BaseCrawler.fetchPage(link)
        .map(this::extractPoem);
  }

  private Poem extractPoem(Document page) {
    Elements poemTitlesEls = page.select(".poem-view-separated > h4");
    Elements poemContentEls = page.select(".poem-view-separated > p");
    String pageTitle = page.select(".page-header h1").html();
    List<String> poemContents =
        poemContentEls.stream()
            .filter(el -> !el.text().isEmpty())
            .map(el -> el.html().replace("<br>", "\n"))
            .collect(Collectors.toList());
    List<PoemContent> poems = new ArrayList<>();
    var index = 0;
    for (Element title : poemTitlesEls) {
      var poemContent = poemContents.get(index);
      var poem = PoemContent.builder().title(title.text()).content(poemContent).build();
      poems.add(poem);
      index++;
    }
    return Poem.builder().title(pageTitle).content(poems).build();
  }
}
