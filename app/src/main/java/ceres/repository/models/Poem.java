package ceres.repository.models;

import ceres.crawler.PoemContent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DBObject;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;

@Slf4j
@Builder
@Data
public class Poem {
  public static String divider = "---";
  @Builder.Default String id = UUID.randomUUID().toString();
  Poet author;
  String url;
  String name;
  List<PoemContent> content;

  public Document toMongoRow() throws JsonProcessingException {
    var mapper = new ObjectMapper();
    var jsonContent = mapper.writeValueAsString(content);

    return new Document("_id", id)
        .append("name", name)
        .append("url", url)
        .append("content", jsonContent)
        .append("author", author.getId());
  }

  public static Poem fromMongo(Document row) throws JsonProcessingException {
    var mapper = new ObjectMapper();
    var contentString = row.get("content").toString();
    var contentList = mapper.readValue(contentString, new TypeReference<List<PoemContent>>(){});
    return Poem
        .builder()
        .id(row.get("_id").toString())
        .name(row.get("name").toString())
        .url(row.get("url").toString())
        .content(contentList)
        .author(Poet.builder().id(row.get("author").toString()).build())
        .build();
  }
}
