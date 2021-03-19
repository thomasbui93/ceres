package ceres.configuration;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class RedisConfiguration {
  String host;
  int port;
  String auth;
}
