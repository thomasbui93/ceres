package com.kbui.ceres.service.crawler.image;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class ImageResult {
  String imageUrl;
  String folder;
}
