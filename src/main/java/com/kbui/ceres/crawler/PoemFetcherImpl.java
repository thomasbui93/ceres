package com.kbui.ceres.crawler;

import com.kbui.ceres.config.CrawlerPoemConfiguration;
import com.kbui.ceres.repository.PoemRepository;
import com.kbui.ceres.repository.PoetRepository;
import com.kbui.ceres.repository.models.PoemEntity;
import com.kbui.ceres.repository.models.Poet;
import com.kbui.ceres.scheduler.PoemCrawlerTask;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PoemFetcherImpl implements PoemFetcher {
  private static final Logger log = LoggerFactory.getLogger(PoemCrawlerTask.class);
  private Poet poet;
  private String poetName;
  private String baseUrl;

  @Autowired private PoetRepository poetRepository;

  @Autowired private PoemRepository poemRepository;

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

  private Future<String> getPoetPage() {
    var uniqueId = String.join("_", getPoetName().split(" "));
    return poetRepository
        .get(getPoetName())
        .recover(
            ex ->
                fetchPoetPage()
                    .compose(
                        pageUrl ->
                            poetRepository.save(
                                Poet.builder()
                                    .poetName(getPoetName())
                                    .poetUrl(pageUrl)
                                    .id(uniqueId)
                                    .build()))
                    .onFailure(
                        e ->
                            log.error(
                                String.format(
                                    "Failed to save poet to db: %s, %s",
                                    getPoetName(), e.getMessage()))))
        .compose(p -> Future.succeededFuture(p.getPoetUrl()));
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
    return BaseCrawler.fetchPage(poetUrl)
        .map(
            page -> {
              var els = page.select(".poem-group-list li a");
              return els.stream()
                  .map(el -> String.format("%s/%s", getBaseUrl(), el.attr("href")))
                  .collect(Collectors.toList());
            });
  }

  private Future<List<String>> fetchPoemLinks(String poetUrl) {
    return poetRepository
        .get(getPoetName())
        .compose(
            poet -> {
              if (poet.getLinks().size() == 0) {
                return getPoemLinks(poetUrl)
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
                                              "Failed to save links to poets db: %s",
                                              ex.getMessage())));
                        })
                    .onFailure(
                        ex ->
                            log.error(
                                String.format(
                                    "Failed to fetch links from poet page: %s", ex.getMessage())));
              } else {
                return Future.succeededFuture(poet.getLinks());
              }
            })
        .onFailure(ex -> log.error("Poet not found."));
  }

  private Future<PoemEntity> getAndSavePoem(String link) {
    return poemRepository
        .get(link)
        .compose(this::updatePoem)
        .recover(ex -> this.createPoem(link));
  }

  private Future<PoemEntity> createPoem(String link) {
    return getPoem(link)
        .compose(
            poem ->
                poemRepository.save(
                    PoemEntity.builder()
                        .name(poem.getTitle())
                        .url(link)
                        .content(poem.getContent())
                        .author(getPoetName())
                        .build()))
        .recover(
            e -> {
              log.error("Failed to fetch poem...Gonna try latter in the next task.", e);
              return Future.succeededFuture(PoemEntity.empty());
            });
  }

  private Future<PoemEntity> updatePoem(PoemEntity poemEntity) {
    if (poemEntity.getContent().size() == 0
        || poemEntity.getName().isEmpty()) { // in case we were detected by bot.
      return getPoem(poemEntity.getUrl())
          .compose(p -> poemRepository.update(poemEntity.getUrl(), p.getContent(), p.getTitle()))
          .recover(
              ex -> {
                log.error(
                    "Failed to update failed fetch attempt...Gonna try latter in the next task.",
                    ex);
                return Future.succeededFuture(PoemEntity.empty());
              });
    } else {
      return Future.succeededFuture(poemEntity);
    }
  }

  private Future<Poem> getPoem(String link) {
    return BaseCrawler.fetchPage(link).map(this::extractPoem);
  }

  private Poem extractPoem(@NonNull Document page) {
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
