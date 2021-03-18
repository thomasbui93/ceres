package ceres.repository;

import ceres.crawler.PoemContent;
import ceres.repository.models.Poem;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.vertx.core.Future;
import java.util.List;
import javax.inject.Inject;
import redis.clients.jedis.Jedis;

public class PoemRepositoryImpl implements PoemRepository {
  final private Jedis redis;

  final private String collectionName = "poems";

  @Inject
  public PoemRepositoryImpl(Jedis redis) {
    this.redis = redis;
  }

  @Override
  public Future<Poem> save(Poem poem) {
    return Future.future((future) -> {
      try {
        this.redis.hset(collectionName, poem.getUrl(), poem.toJson());
        future.complete(poem);
      } catch (JsonProcessingException ex) {
        future.fail(ex);
      }
    });
  }

  @Override
  public Future<Poem> update(String poemUrl, List<PoemContent> content) {
    return Future.future((future) -> {
      try {
        var poem = Poem.fromJson(this.redis.hget(collectionName, poemUrl));
        poem.setContent(content);
        this.redis.hset(collectionName, poemUrl, poem.toJson());
        future.complete(poem);
      } catch (JsonProcessingException ex) {
        future.fail(ex);
      }
    });
  }

  @Override
  public Future<Poem> get(String poemUrl) {
    return Future.future((future) -> {
      try {
        var poem = Poem.fromJson(this.redis.hget(collectionName, poemUrl));
        future.complete(poem);
      } catch (JsonProcessingException ex) {
        future.fail(ex);
      }
    });
  }
}
