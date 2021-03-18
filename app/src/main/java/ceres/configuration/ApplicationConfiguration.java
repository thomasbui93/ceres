package ceres.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class ApplicationConfiguration {
  CrawlerConfiguration crawler;

  public static ApplicationConfiguration loadFrom(String fileName) throws IOException {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    File file = new File(Objects.requireNonNull(classLoader.getResource(fileName)).getFile());

    ObjectMapper om = new ObjectMapper(new YAMLFactory());

    return om.readValue(file, ApplicationConfiguration.class);
  }
}
