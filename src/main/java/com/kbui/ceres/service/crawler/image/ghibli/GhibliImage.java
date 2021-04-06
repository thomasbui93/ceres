package com.kbui.ceres.service.crawler.image.ghibli;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class GhibliImage {
  String imageUrl;
  String movie;
}
