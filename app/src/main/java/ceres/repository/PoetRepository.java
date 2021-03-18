package ceres.repository;

import ceres.repository.models.Poet;
import io.vertx.core.Future;

public interface PoetRepository {
  public Future<Poet> save(Poet poet);
  public Future<Boolean> delete(String poetName);
  public Future<Poet> get(String poetName);
}
