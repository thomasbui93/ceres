package ceres.repository;

import ceres.repository.models.Poem;
import io.vertx.core.Future;

public interface PoemRepository {
  public Future<Poem> save(Poem poem);
  public Future<Poem> update(String poemUrl, String content);
}
