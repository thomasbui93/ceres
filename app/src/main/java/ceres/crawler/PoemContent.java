package ceres.crawler;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PoemContent {
  String title;
  String content;
}
