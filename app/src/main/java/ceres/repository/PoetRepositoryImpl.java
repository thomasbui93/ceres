package ceres.repository;

import ceres.exception.crawler.AuthorPageNotFoundException;
import ceres.repository.models.Poet;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import io.vertx.core.Future;
import javax.inject.Inject;
import org.bson.Document;

public class PoetRepositoryImpl implements PoetRepository {
  private final MongoCollection<Document> collection;

  @Inject
  public PoetRepositoryImpl(MongoDatabase db) {
    this.collection = db.getCollection("poets");
  }

  public Future<Poet> save(Poet poet) {
    return Future.future((future) -> {
      try {
        this.collection.insertOne(poet.toMongoRow());
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
        var filter = new Document("_id", id);
        this.collection.findOneAndDelete(filter);
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
        var filter = new Document("_id", id);
        var poet = this.collection.find(filter).first();
        if (poet == null) {
          future.fail(new AuthorPageNotFoundException());
        } else {
          future.complete(Poet.fromMongo(poet));
        }
      } catch (MongoException ex) {
        future.fail(ex);
      }
    });
  }
}
