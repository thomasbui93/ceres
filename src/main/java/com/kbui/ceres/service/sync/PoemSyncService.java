package com.kbui.ceres.service.sync;

import com.kbui.ceres.config.CrawlerPoemConfiguration;
import com.kbui.ceres.repository.PoemRepository;
import com.kbui.ceres.repository.PoetRepository;
import com.kbui.ceres.repository.models.Poet;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

@Service
public class PoemSyncService {
  private static final Logger log = LoggerFactory.getLogger(PoemSyncService.class);

  @Autowired
  CrawlerPoemConfiguration poemConfiguration;

  @Autowired
  PoetRepository poetRepository;

  @Autowired
  PoemRepository poemRepository;

  @Autowired
  MongoTemplate mongoTemplate;

  public Future<Boolean> syncRedisToMongoDb() {
    var authors = poemConfiguration.getAuthors();
    var futures = authors.stream().map(this::syncDataPerAuthor)
        .toArray(Future[]::new);
    return CompositeFuture.all(Arrays.asList(futures))
        .map(s -> true);
  }

  private Future<Boolean> syncDataPerAuthor(String author) {
    return poetRepository.get(author)
        .map(poet -> {
          try {
            var poetDocument = mongoTemplate.insert(poet.toPoetDocument(), "poets");
            log.info("Poet does not exist, created one. {}", author);
            return Pair.of(poet, poetDocument);
          } catch (Exception ex) {
            var query = new Query(Criteria.where("url").is(poet.getPoetUrl()));
            var poetDoc = mongoTemplate.findOne(query, PoetDocument.class, "poets");
            if (poetDoc == null) {
              log.error("Failed to sync poet to db", ex);
              return Pair.of(poet, PoetDocument.builder().build());
            }
            return Pair.of(poet, poetDoc);
          }
        })
        .compose(r -> syncAuthorPoems(r.getFirst(), r.getSecond()));
  }

  private Future<Boolean> syncAuthorPoems(Poet poet, PoetDocument poetDocument) {
    var links = poet.getLinks();
    var futures = links.stream().map(link -> mapPoemToMongoDb(link, poetDocument))
        .toArray(Future[]::new);
    if (poetDocument.getId() == null) return Future.failedFuture("Empty poet.");
    return CompositeFuture.all(Arrays.asList(futures))
        .compose(s -> {
          if (s.failed()) {
            log.error("Failed to sync author poems to mongodb: {}", s.causes());
          } else {
            log.info("Done sync author {} poems to mongodb.", poet.getPoetName());
          }
          return Future.succeededFuture(s.succeeded());
        });
  }

  private Future<PoemDocument> mapPoemToMongoDb(String link, PoetDocument poetDocument) {
    return poemRepository
        .get(link)
        .compose(
            poemEntity -> {
              try {
                if (poemEntity.getContent().size() == 0) {
                  return Future.succeededFuture();
                }
                var poemDocument = poemEntity.toPoemDocument(poetDocument);
                var poem = mongoTemplate.insert(poemDocument, "poems");
                return Future.succeededFuture(poem);
              } catch (Exception ex) {
                log.info("Failed to sync poem to db: {}. Skip it.", ex.getMessage());
                return Future.succeededFuture(PoemDocument.builder().build());
              }
            });
  }
}
