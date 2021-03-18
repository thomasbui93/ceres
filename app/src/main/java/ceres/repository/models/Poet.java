package ceres.repository.models;

import com.mongodb.DBObject;
import lombok.Builder;
import lombok.Data;
import org.bson.Document;

@Builder
@Data
public class Poet {
  String id;
  String poetName;
  String poetUrl;

  public Document toMongoRow() {
    var uniqueId = String.join("_",poetName.split(" "));
    return new Document("_id", uniqueId)
        .append("name", poetName)
        .append("url", poetUrl);
  }

  public static Poet fromMongo(Document row) {
    return Poet
        .builder()
        .id(row.get("_id").toString())
        .poetName(row.get("name").toString())
        .poetUrl(row.get("url").toString())
        .build();
  }
}
