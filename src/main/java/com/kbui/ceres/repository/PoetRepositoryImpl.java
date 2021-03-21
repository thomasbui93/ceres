package com.kbui.ceres.repository;

import com.kbui.ceres.exception.crawler.AuthorPageNotFoundException;
import com.kbui.ceres.repository.models.Poet;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.vertx.core.Future;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

@Component
public class PoetRepositoryImpl implements PoetRepository {

  @Autowired
  private Jedis redis;

  final private String collectionName = "poets";

  public Future<Poet> save(Poet poet) {
    return Future.future((future) -> {
      try {
        this.redis.hset(collectionName, poet.getId(), poet.toJson());
        future.complete(poet);
      } catch (JsonProcessingException ex) {
        future.fail(ex);
      }
    });
  }

  public Future<Boolean> delete(String poetName) {
    return Future.future((future) -> {
      try {
        var id = String.join("_",poetName.split(" "));
        this.redis.hdel(collectionName, id);
        future.complete(true);
      } catch (Exception ex) {
        future.fail(ex);
      }
    });
  }

  public Future<Poet> get(String poetName) {
    return Future.future((future) ->  {
      try {
        var id = String.join("_",poetName.split(" "));
        var poet = this.redis.hget(collectionName, id);
        if (poet == null) {
          future.fail(new AuthorPageNotFoundException());
        } else {
          future.complete(Poet.fromJson(poet));
        }
      } catch (JsonProcessingException ex) {
        future.fail(ex);
      }
    });
  }
}
