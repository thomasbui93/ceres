package com.kbui.ceres.repository.models;

import com.kbui.ceres.service.crawler.entity.PoemContent;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.util.List;

public class PoemDeserializer extends StdDeserializer<PoemEntity> {
  public PoemDeserializer() {
    this(null);
  }

  @Override
  public PoemEntity deserialize(JsonParser jp, DeserializationContext ctx)
      throws IOException, JsonProcessingException {
    JsonNode node = jp.getCodec().readTree(jp);
    ObjectMapper mapper = new ObjectMapper();
    List<PoemContent> content = mapper.readValue(node.get("content").toString(), new TypeReference<>() {});

    return PoemEntity.builder()
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
