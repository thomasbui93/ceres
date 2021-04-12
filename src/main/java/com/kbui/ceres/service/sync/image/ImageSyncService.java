package com.kbui.ceres.service.sync.image;

import com.kbui.ceres.service.crawler.image.ImageResult;
import java.util.List;
import java.util.concurrent.Future;

interface ImageSyncService {
  Future<Boolean> sync(List<ImageResult> imageResults);
}
