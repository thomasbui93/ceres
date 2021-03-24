package com.kbui.ceres.scheduler;

import com.kbui.ceres.config.CrawlerPoemConfiguration;
import com.kbui.ceres.service.crawler.PoemFetcher;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PoemCrawlerTask {
  @Autowired private PoemFetcher poemFetcher;

  @Autowired
  CrawlerPoemConfiguration poemConfiguration;

  private static final Logger log = LoggerFactory.getLogger(PoemCrawlerTask.class);

  private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

  @Scheduled(fixedRate = 1000 * 60 * 60)
  public void fetchPoems() {
    log.info("About to fetch poems, starting now: {}", dateFormat.format(new Date()));
    var authors = poemConfiguration.getAuthors();
    var baseUrl = poemConfiguration.getBaseUrl();

    var futures = authors
        .stream()
        .map(author -> poemFetcher
          .fetchPoems(author, baseUrl)
            .onSuccess(s -> log.info("Done fetching for poems: {}", author))
            .onFailure(e -> log.error("Failed to fetch poems", e))
        ).toArray(Future[]::new);

    CompositeFuture.all(Arrays.asList(futures))
        .onComplete(r -> log.info("Done fetching poems for all authors"));
  }
}
