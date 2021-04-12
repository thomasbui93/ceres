package com.kbui.ceres.service.api;

import com.cloudinary.Cloudinary;
import com.kbui.ceres.service.crawler.image.ImageResult;
import io.vertx.core.Future;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CloudinaryApi implements UploadApi {
  @Autowired private Cloudinary cloudinary;

  @Override
  public Future<String> upload(ImageResult imageResult) {
    return Future.future(
        r -> {
          try {
            Map<String, String> config = new HashMap<String, String>();
            config.put("folder", imageResult.getFolder());

            var uploadResult = cloudinary.uploader().upload(imageResult.getImageUrl(), config);
            String url = (String) uploadResult.getOrDefault("url", "");

            r.complete(url);
          } catch (IOException e) {
            r.fail(e);
          }
        });
  }
}
