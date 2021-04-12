package com.kbui.ceres.service.sync.image;

import com.kbui.ceres.service.crawler.image.ImageResult;
import java.util.List;
import java.util.concurrent.Future;
import org.springframework.stereotype.Component;

@Component
public class CloudinarySync implements ImageSyncService {

  @Override
  public Future<Boolean> sync(List<ImageResult> imageResults) {
    return null;
  }
}
