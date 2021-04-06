package com.kbui.ceres.repository;

import com.kbui.ceres.service.crawler.entity.PoemContent;
import com.kbui.ceres.repository.models.PoemEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.kbui.ceres.scheduler.PoemCrawlerTask;
import io.vertx.core.Future;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

@Component
public class PoemRepositoryImpl implements PoemRepository {
  private static final Logger log = LoggerFactory.getLogger(PoemCrawlerTask.class);

  @Autowired
  private Jedis redis;

  final private String collectionName = "poems";

  @Override
  public Future<PoemEntity> save(PoemEntity poem) {
    return Future.future((future) -> {
      try {
        this.redis.hset(collectionName, poem.getUrl(), poem.toJson());
        future.complete(poem);
      } catch (JsonProcessingException ex) {
        log.error(String.format("Failed to save poem %s", poem), ex);
        future.fail(ex);
      }
    });
  }

  @Override
  public Future<PoemEntity> update(String poemUrl, List<PoemContent> content, String title) {
    return Future.future((future) -> {
      try {
        var poem = PoemEntity.fromJson(this.redis.hget(collectionName, poemUrl));
        poem.setContent(content);
        poem.setName(title);
        this.redis.hset(collectionName, poemUrl, poem.toJson());
        future.complete(poem);
      } catch (JsonProcessingException ex) {
        log.error(String.format("Failed to encoding poem %s", content), ex);
        future.fail(ex);
      }
    });
  }

  @Override
  public Future<PoemEntity> get(String poemUrl) {
    return Future.future((future) -> {
      try {
        var poem = PoemEntity.fromJson(this.redis.hget(collectionName, poemUrl));
        future.complete(poem);
      } catch (JsonProcessingException ex) {
        log.error(String.format("Failed to get poem with url %s", poemUrl), ex);
        future.fail(ex);
      }
    });
  }
}
