package com.kbui.ceres.scheduler;

import com.kbui.ceres.service.sync.poem.PoemSyncService;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PoemSyncTask {
  @Autowired
  private PoemSyncService service;

  private static final Logger log = LoggerFactory.getLogger(PoemCrawlerTask.class);

  private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

  @Scheduled(fixedRate = 2 * 1000 * 60 * 60)
  public void syncPoems() {
    log.info("About to sync poems, starting now: {}", dateFormat.format(new Date()));
    service.syncRedisToMongoDb()
        .onFailure(s -> log.error("Failed to sync poems to mongodb."))
        .onSuccess(s -> log.info("Successfully sync poems to mongodb."));
  }
}
