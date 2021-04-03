package com.kbui.ceres.service.crawler;

import com.kbui.ceres.repository.PoetRepository;
import com.kbui.ceres.repository.models.Poet;
import com.kbui.ceres.service.crawler.parser.PoemParserService;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PoemFetcherImpl implements PoemFetcher {
  private static final Logger log = LoggerFactory.getLogger(PoemFetcherImpl.class);
  private Poet poet;
  private String poetName;
  private String baseUrl;

  @Autowired private PoetRepository poetRepository;

  @Autowired private PoemParserService poemParserService;

  public String getPoetName() {
    return poetName;
  }

  public String getBaseUrl() {
    return baseUrl;
  }

  public Future<Boolean> fetchPoems(String poetName, String baseUrl) {
    this.poetName = poetName;
    this.baseUrl = baseUrl;
    return getPoetPage()
        .compose(this::getPoemLinks)
        .compose(
            links -> {
              var futures =
                  Arrays.asList(
                      links.stream()
                          .map(link -> poemParserService.getAndSavePoem(link, this.poetName))
                          .toArray(Future[]::new));
              return Future.future(
                  (future) ->
                      CompositeFuture.all(futures).onComplete(r -> future.complete(r.succeeded())));
            });
  }

  private Future<String> getPoetPage() {
    return poetRepository
        .get(getPoetName())
        .recover(
            ex ->
                fetchPoetPage()
                    .compose(this::savePoet)
                    .onFailure(
                        e ->
                            log.error(
                                String.format(
                                    "Failed to save poet to db: %s, %s",
                                    getPoetName(), e.getMessage()))))
        .compose(p -> Future.succeededFuture(p.getPoetUrl()));
  }

  private Future<Poet> savePoet(String poetPageUrl) {
    var uniqueId = String.join("_", getPoetName().split(" "));
    return poetRepository.save(
            Poet.builder()
                .poetName(getPoetName())
                .poetUrl(poetPageUrl)
                .id(uniqueId)
                .build());
  }

  private Future<String> fetchPoetPage() {
    String url =
        String.format(
            "%s/qsearch.xml.php" + "?Core=author&Field=Name&Value=%s&Page=0",
            getBaseUrl(), URLEncoder.encode(getPoetName()));
    return BaseCrawler.fetchPage(url)
        .map(page -> page.select("a"))
        .map(links -> String.format("%s/%s", getBaseUrl(), links.get(0).attr("href")));
  }

  private Future<List<String>> getPoemLinks(String poetUrl) {
    return poetRepository
        .get(getPoetName())
        .compose(
            poet ->
                poet.getLinks().size() == 0
                    ? remotePoemLinks(poetUrl)
                    : Future.succeededFuture(poet.getLinks()))
        .onFailure(ex -> log.error("Poet not found."));
  }

  private Future<List<String>> remotePoemLinks(String poetUrl) {
    return fetchPoemLinks(poetUrl)
        .compose(
            links -> {
              poet.setLinks(links);
              return poetRepository
                  .save(poet)
                  .compose(re -> Future.succeededFuture(links))
                  .onFailure(
                      ex ->
                          log.error(
                              String.format(
                                  "Failed to save links to poets db: %s", ex.getMessage())));
            })
        .onFailure(
            ex ->
                log.error(
                    String.format("Failed to fetch links from poet page: %s", ex.getMessage())));
  }

  private Future<List<String>> fetchPoemLinks(String poetUrl) {
    return BaseCrawler.fetchPage(poetUrl)
        .map(
            page -> {
              var els = page.select(".poem-group-list li a");
              return els.stream()
                  .map(el -> String.format("%s/%s", getBaseUrl(), el.attr("href")))
                  .collect(Collectors.toList());
            });
  }
}
