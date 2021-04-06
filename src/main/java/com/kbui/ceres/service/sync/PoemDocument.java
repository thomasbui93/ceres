package com.kbui.ceres.service.sync;

import com.kbui.ceres.service.crawler.poem.entity.PoemContent;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@Builder
public class PoemDocument {
  @Id
  String id;
  String url;
  String name;
  List<PoemContent> content;

  @DBRef
  PoetDocument poet;

  @Version Long version;
}
