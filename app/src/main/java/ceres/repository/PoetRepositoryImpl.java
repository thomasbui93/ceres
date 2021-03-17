package ceres.repository;

import ceres.repository.models.Poet;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import io.vertx.core.Future;
import javax.inject.Inject;

public class PoetRepositoryImpl implements PoetRepository {
  private final DBCollection collection;

  @Inject
  public PoetRepositoryImpl(MongoClient mongoClient) {
    var db = mongoClient.getDB(System.getenv("MONGO_DATABASE_NAME"));
    this.collection = db.getCollection("poets");
  }

  public Future<Poet> save(Poet poet) {
    return Future.future((future) -> {
      try {
        this.collection.save(poet.toMongoRow());
        future.complete(poet);
      } catch (MongoException ex) {
        future.fail(ex);
      }
    });
  }

  public Future<Boolean> delete(String poetName) {
    return Future.future((future) -> {
      try {
        var id = String.join("_",poetName.split(" "));
        var filter = new BasicDBObject("_id", id);
        this.collection.findAndRemove(filter);
        future.complete(true);
      } catch (MongoException ex) {
        future.fail(ex);
      }
    });
  }

  public Future<Poet> get(String poetName) {
    return Future.future((future) ->  {
      try {
        var id = String.join("_",poetName.split(" "));
        var filter = new BasicDBObject("_id", id);
        var poet = this.collection.findOne(filter);
        future.complete(Poet.fromMongo(poet));
      } catch (MongoException ex) {
        future.fail(ex);
      }
    });
  }
}
