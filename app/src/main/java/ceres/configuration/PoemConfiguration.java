package ceres.configuration;

import java.util.List;
import lombok.Data;

@Data
public class PoemConfiguration {
  String baseUrl;
  List<String> authors;
}
