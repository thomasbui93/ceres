package com.kbui.ceres.crawler;

import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Poem {
  String title;
  List<PoemContent> content;
}
