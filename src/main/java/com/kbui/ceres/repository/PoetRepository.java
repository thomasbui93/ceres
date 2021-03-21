package com.kbui.ceres.repository;

import com.kbui.ceres.repository.models.Poet;
import io.vertx.core.Future;
import org.springframework.stereotype.Service;

@Service
public interface PoetRepository {
  public Future<Poet> save(Poet poet);
  public Future<Boolean> delete(String poetName);
  public Future<Poet> get(String poetName);
}
