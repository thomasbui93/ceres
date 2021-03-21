package com.kbui.ceres.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

@Configuration
@Slf4j
public class RedisConnectionConfiguration {
  @Bean
  public Jedis redis() {
    try {
      var redisConf = getRedisConfig();
      var jedis = new Jedis(redisConf.getHost(), redisConf.getPort());
      jedis.auth(redisConf.getAuth());
      return jedis;
    } catch (Exception ex) {
      log.error(String.format("Failed to connect to redis: %s", ex.getMessage()));
      return null;
    }
  }

  public RedisConfiguration getRedisConfig() {
    return RedisConfiguration
        .builder()
        .host(System.getenv("REDIS_HOST"))
        .port(Integer.parseInt(System.getenv("REDIS_PORT")))
        .auth(System.getenv("REDIS_AUTH"))
        .build();
  }
}
