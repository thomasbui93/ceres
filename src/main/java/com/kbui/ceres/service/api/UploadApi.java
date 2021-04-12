package com.kbui.ceres.service.api;

import com.kbui.ceres.service.crawler.image.ImageResult;
import io.vertx.core.Future;

public interface UploadApi {
  Future<String> upload(ImageResult imageResult);
}
