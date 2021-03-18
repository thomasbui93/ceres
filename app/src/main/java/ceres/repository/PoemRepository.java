package ceres.repository;

import ceres.crawler.PoemContent;
import ceres.repository.models.Poem;
import io.vertx.core.Future;
import java.util.List;

public interface PoemRepository {
  public Future<Poem> save(Poem poem);
  public Future<Poem> update(String poemUrl, List<PoemContent> content);
  public Future<Poem> get(String poemUrl);
}
