package com.kbui.ceres.service.sync;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@Builder
public class PoetDocument {
  @Id
  String id;
  String poetName;
  String poetUrl;
  @Version
  Long version;
}
