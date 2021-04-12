package com.kbui.ceres.scheduler;

import com.kbui.ceres.service.crawler.image.ImageFetcher;
import com.kbui.ceres.service.sync.image.CloudinarySync;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ImageCrawlerTask {
  private static final Logger log = LoggerFactory.getLogger(ImageCrawlerTask.class);

  @Autowired
  ImageFetcher imageFetcher;

  @Autowired
  CloudinarySync cloudinarySync;

  @Scheduled(fixedRate = 1000 * 60 * 60)
  public void fetchImages() {
    imageFetcher
      .fetchImages()
      .map(images -> cloudinarySync.sync(images))
      .onFailure(r -> log.error("Failed to fetch and sync images", r.getCause()))
      .onComplete(r -> log.info("Finished fetch and sync images"));
  }
}
