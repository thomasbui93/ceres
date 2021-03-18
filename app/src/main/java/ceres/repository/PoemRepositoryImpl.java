package ceres.repository;

import ceres.exception.crawler.AuthorPageNotFoundException;
import ceres.exception.crawler.PoemLinksFetchingException;
import ceres.repository.models.Poem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.vertx.core.Future;
import javax.inject.Inject;

public class PoemRepositoryImpl implements PoemRepository {
  final private MongoCollection<org.bson.Document> collection;

  @Inject
  public PoemRepositoryImpl(MongoDatabase db) {
    this.collection = db.getCollection("poems");
  }

  @Override
  public Future<Poem> save(Poem poem) {
    return Future.future((future) -> {
      try {
        this.collection.insertOne(poem.toMongoRow());
        future.complete(poem);
      } catch (MongoException | JsonProcessingException ex) {
        future.fail(ex);
      }
    });
  }

  @Override
  public Future<Poem> update(String poemUrl, String content) {
    return Future.future((future) -> {
      try {
        var filter = new BasicDBObject("url", poemUrl);
        var update = new BasicDBObject("content", content);
        var poem = this.collection.findOneAndUpdate(filter, update);
        if (poem == null) {
          future.fail(new PoemLinksFetchingException());
        } else {
          future.complete(Poem.fromMongo(poem));
        }
      } catch (MongoException | JsonProcessingException ex) {
        future.fail(ex);
      }
    });
  }
}
