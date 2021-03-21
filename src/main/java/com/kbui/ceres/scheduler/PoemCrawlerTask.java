package com.kbui.ceres.scheduler;

import com.kbui.ceres.crawler.PoemFetcher;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PoemCrawlerTask {
  @Autowired private PoemFetcher poemFetcher;

  private static final Logger log = LoggerFactory.getLogger(PoemCrawlerTask.class);

  private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

  @Scheduled(fixedRate = 1000 * 60 * 60)
  public void fetchPoems() {
    log.info("About to fetch poems, starting now: {}", dateFormat.format(new Date()));
    poemFetcher
        .fetchPoems()
        .onSuccess(s -> log.info("Done fetching for poems: {}", s))
        .onFailure(e -> log.error("Failed to fetch poems", e));
  }
}
