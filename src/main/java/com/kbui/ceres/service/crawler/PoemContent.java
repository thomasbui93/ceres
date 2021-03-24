package com.kbui.ceres.service.crawler;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class PoemContent {
  @JsonProperty("title")
  String title;

  @JsonProperty("content")
  String content;
}
