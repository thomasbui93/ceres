package ceres.repository.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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
}
