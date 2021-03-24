package com.kbui.ceres.repository;

import com.kbui.ceres.service.crawler.PoemContent;
import com.kbui.ceres.repository.models.PoemEntity;
import io.vertx.core.Future;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface PoemRepository {
  public Future<PoemEntity> save(PoemEntity poem);
  public Future<PoemEntity> update(String poemUrl, List<PoemContent> content, String title);
  public Future<PoemEntity> get(String poemUrl);
}
