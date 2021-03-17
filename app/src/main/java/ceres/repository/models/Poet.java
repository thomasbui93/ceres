package ceres.repository.models;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Poet {
  String id;
  String poetName;
  String poetUrl;

  public DBObject toMongoRow() {
    var uniqueId = String.join("_",poetName.split(" "));
    return new BasicDBObject("_id", uniqueId)
        .append("name", poetName)
        .append("url", poetUrl);
  }

  public static Poet fromMongo(DBObject row) {
    return Poet
        .builder()
        .id(row.get("_id").toString())
        .poetName(row.get("name").toString())
        .poetUrl(row.get("url").toString())
        .build();
  }
}
