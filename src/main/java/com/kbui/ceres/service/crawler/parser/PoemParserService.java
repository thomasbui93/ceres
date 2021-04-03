package com.kbui.ceres.service.crawler.parser;

import com.kbui.ceres.exception.crawler.PoemParsingException;
import com.kbui.ceres.repository.PoemRepository;
import com.kbui.ceres.repository.models.PoemEntity;
import com.kbui.ceres.service.crawler.BaseCrawler;
import com.kbui.ceres.service.crawler.entity.Poem;
import io.vertx.core.Future;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PoemParserService {
  private static final Logger log = LoggerFactory.getLogger(PoemParserService.class);

  @Autowired private PoemRepository poemRepository;

  public Future<PoemEntity> getAndSavePoem(String link, String poetName) {
    return poemRepository
        .get(link)
        .compose(this::updatePoem)
        .recover(ex -> this.createPoem(link, poetName))
        .onComplete(
            r -> {
              if (r.result().getId() != null) {
                log.info("Link fetching done: {}", link);
              }
            });
  }

  private Poem parse(Document page) throws PoemParsingException {
    var parser = getPoemParser(page);
    return parser.parseDoc(page);
  }

  private PoemParser getPoemParser(Document page) {
    Elements poemContentEls = page.select(".poem-view-separated > p");
    return poemContentEls.size() == 0 ? new VietnameseOriginParser() : new ChineseOriginParser();
  }

  private Future<PoemEntity> createPoem(String link, String poetName) {
    return getPoem(link)
        .compose(
            poem ->
                poemRepository.save(
                    PoemEntity.builder()
                        .name(poem.getTitle())
                        .url(link)
                        .content(poem.getContent())
                        .author(poetName)
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
                    "Failed to update failed fetch attempt...Gonna try latter in the next task.");
                return Future.succeededFuture(PoemEntity.empty());
              });
    } else {
      return Future.succeededFuture(poemEntity);
    }
  }

  private Future<Poem> getPoem(String link) {
    return BaseCrawler.fetchPage(link)
        .flatMap(
            page -> {
              try {
                var poem = this.parse(page);
                return Future.succeededFuture(poem);
              } catch (PoemParsingException ex) {
                log.error("Failed to fetch poem in this link: {}", link);
                return Future.failedFuture(ex);
              }
            });
  }
}
