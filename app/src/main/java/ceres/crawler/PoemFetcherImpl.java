package ceres.crawler;

import ceres.configuration.PoemConfiguration;
import ceres.repository.PoemRepository;
import ceres.repository.PoetRepository;
import ceres.repository.models.Poet;
import com.google.inject.Inject;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@Slf4j
public class PoemFetcherImpl implements PoemFetcher {
  private final String poetName;
  private final String baseUrl;
  private Poet poet;

  private final PoetRepository poetRepository;
  private final PoemRepository poemRepository;

  @Inject
  public PoemFetcherImpl(
      PoemConfiguration poemConfiguration,
      PoetRepository poetRepository,
      PoemRepository poemRepository) {
    this.poetName = poemConfiguration.getAuthors().get(0);
    this.baseUrl = poemConfiguration.getBaseUrl();
    this.poetRepository = poetRepository;
    this.poemRepository = poemRepository;
  }

  private Future<String> getPoetPage() {
    var uniqueId = String.join("_", poetName.split(" "));
    return poetRepository.get(poetName)
        .recover(ex -> fetchPoetPage()
            .compose(pageUrl -> poetRepository.save(Poet.builder()
                .poetName(poetName)
                .poetUrl(pageUrl)
                .id(uniqueId)
                .build())
            )
            .onFailure(e -> log.error(String.format("Failed to save poet to db: %s, %s", poetName, e.getMessage())))
        )
        .compose(p -> Future.succeededFuture(p.getPoetUrl()));
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
        .map(
            page -> {
              var els = page.select(".poem-group-list li a");
              return els.stream()
                  .map(el -> String.format("%s/%s", this.baseUrl, el.attr("href")))
                  .collect(Collectors.toList());
            });
  }

  public Future<Boolean> fetchPoems() {
    return getPoetPage()
        .compose(this::fetchPoemLinks)
        .compose(
            links -> {
              var futures =
                  Arrays.asList(links.stream().map(this::getAndSavePoem).toArray(Future[]::new));
              return Future.future(
                  (future) ->
                      CompositeFuture.all(futures).onComplete(r -> future.complete(r.succeeded())));
            });
  }

  private Future<List<String>> fetchPoemLinks(String poetUrl) {
    return poetRepository
        .get(poetName)
        .compose(poet -> {
          if (poet.getLinks().size() == 0) {
            return getPoemLinks(poetUrl)
                .compose(links -> {
                  poet.setLinks(links);
                  return poetRepository
                      .save(poet)
                      .compose(re -> Future.succeededFuture(links))
                      .onFailure(ex -> log.error(String.format("Failed to save links to poets db: %s", ex.getMessage())));
                })
                .onFailure(ex -> log.error(String.format("Failed to fetch links from poet page: %s", ex.getMessage())));
          } else {
            return Future.succeededFuture(poet.getLinks());
          }
        })
        .onFailure(ex -> log.error("Poet not found."));
  }

  private Future<ceres.repository.models.Poem> getAndSavePoem(String link) {
    return poemRepository.get(link)
        .recover(ex -> getPoem(link)
            .compose(
                poem ->
                    poemRepository.save(
                        ceres.repository.models.Poem.builder()
                            .name(poem.getTitle())
                            .url(link)
                            .content(poem.getContent())
                            .author(poetName)
                            .build()))
        );
  }

  private Future<Poem> getPoem(String link) {
    return BaseCrawler.fetchPage(link).map(this::extractPoem);
  }

  private Poem extractPoem(@org.jetbrains.annotations.NotNull Document page) {
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
