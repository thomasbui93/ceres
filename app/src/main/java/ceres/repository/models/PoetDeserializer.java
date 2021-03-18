package ceres.repository.models;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.util.List;

public class PoetDeserializer extends StdDeserializer<Poet> {
  public PoetDeserializer() {
    this(null);
  }

  @Override
  public Poet deserialize(JsonParser jp, DeserializationContext ctx)
      throws IOException, JsonProcessingException {
    JsonNode poetNode = jp.getCodec().readTree(jp);
    ObjectMapper mapper = new ObjectMapper();
    List links = mapper.readValue(poetNode.get("links").toString(), List.class);

    return Poet.builder()
        .id(poetNode.get("id").textValue())
        .poetName(poetNode.get("poetName").textValue())
        .poetUrl(poetNode.get("poetUrl").textValue())
        .links(links)
        .build();
  }

  public PoetDeserializer(Class<?> vc) {
    super(vc);
  }
}
