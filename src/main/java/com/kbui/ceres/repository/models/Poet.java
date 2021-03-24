package com.kbui.ceres.repository.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.kbui.ceres.service.sync.PoetDocument;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@JsonDeserialize(using = PoetDeserializer.class)
@Builder
@Data
public class Poet {
  String id;
  String poetName;
  String poetUrl;
  @Builder.Default List<String> links = List.of();

  public String toJson() throws JsonProcessingException {
    var mapper = new ObjectMapper();
    return mapper.writeValueAsString(this);
  }

  public static Poet fromJson(String json) throws JsonProcessingException {
    var mapper = new ObjectMapper();
    return mapper.readValue(json, Poet.class);
  }

  public PoetDocument toPoetDocument() {
    return PoetDocument.builder()
        .poetName(poetName)
        .poetUrl(poetUrl)
        .build();
  }
}
