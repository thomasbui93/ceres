package com.kbui.ceres.repository.models;

import com.kbui.ceres.service.crawler.entity.PoemContent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.kbui.ceres.service.sync.PoemDocument;
import com.kbui.ceres.service.sync.PoetDocument;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
@Data
@JsonDeserialize(using = PoemDeserializer.class)
public class PoemEntity {
  @Builder.Default String id = UUID.randomUUID().toString();
  String author;
  String url;
  String name;
  List<PoemContent> content;

  public String toJson() throws JsonProcessingException {
    var mapper = new ObjectMapper();
    return mapper.writeValueAsString(this);
  }

  public static PoemEntity fromJson(String json) throws JsonProcessingException {
    var mapper = new ObjectMapper();
    return mapper.readValue(json, PoemEntity.class);
  }

  public static PoemEntity empty() {
    return PoemEntity.builder().build();
  }

  public PoemDocument toPoemDocument(PoetDocument poetDocument) {
    return PoemDocument.builder()
        .poet(poetDocument)
        .content(content)
        .url(url)
        .name(name)
        .build();
  }
}
