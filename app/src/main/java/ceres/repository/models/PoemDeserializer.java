package ceres.repository.models;

import ceres.crawler.PoemContent;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.util.List;

public class PoemDeserializer extends StdDeserializer<Poem> {
  public PoemDeserializer() {
    this(null);
  }

  @Override
  public Poem deserialize(JsonParser jp, DeserializationContext ctx)
      throws IOException, JsonProcessingException {
    JsonNode node = jp.getCodec().readTree(jp);
    ObjectMapper mapper = new ObjectMapper();
    List<PoemContent> content = mapper.readValue(node.get("content").toString(), new TypeReference<>() {});

    return Poem.builder()
        .url(node.get("url").textValue())
        .author(node.get("author").textValue())
        .name(node.get("name").textValue())
        .content(content)
        .id(node.get("id").textValue())
        .build();
  }

  public PoemDeserializer(Class<?> vc) {
    super(vc);
  }
}
