package ceres.repository;

import ceres.repository.models.Poem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import io.vertx.core.Future;
import java.util.concurrent.CompletableFuture;
import javax.inject.Inject;

public class PoemRepositoryImpl implements PoemRepository {
  final private DBCollection collection;

  @Inject
  public PoemRepositoryImpl(MongoClient mongoClient) {
    var db = mongoClient.getDB(System.getenv("MONGO_DATABASE_NAME"));
    this.collection = db.getCollection("poem");
  }

  @Override
  public Future<Poem> save(Poem poem) {
    return Future.future((future) -> {
      try {
        this.collection.save(poem.toMongoRow());
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
        var result = this.collection.findAndModify(filter, update);
        var poem = Poem.fromMongo(result);
        future.complete(poem);
      } catch (MongoException | JsonProcessingException ex) {
        future.fail(ex);
      }
    });
  }
}
