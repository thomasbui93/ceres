package com.kbui.ceres.config;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties("crawler.poem")
@Component
@Data
public class CrawlerPoemConfiguration {
  private String baseUrl;
  private List<String> authors;
}
