package ceres;

import ceres.configuration.ApplicationConfiguration;
import ceres.configuration.PoemConfiguration;
import ceres.configuration.RedisConfiguration;
import ceres.crawler.PoemFetcher;
import ceres.crawler.PoemFetcherImpl;
import ceres.repository.PoemRepository;
import ceres.repository.PoemRepositoryImpl;
import ceres.repository.PoetRepository;
import ceres.repository.PoetRepositoryImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import io.github.cdimascio.dotenv.Dotenv;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

@Slf4j
public class AppModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(PoetRepository.class).to(PoetRepositoryImpl.class);
    bind(PoemRepository.class).to(PoemRepositoryImpl.class);
    bind(PoemFetcher.class).to(PoemFetcherImpl.class);
  }

  @Provides
  @Singleton
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

  @Provides
  public PoemConfiguration poemConfiguration() throws IOException {
    var appConfig = ApplicationConfiguration.loadFrom("app.yaml");
    return appConfig.getCrawler().getPoem();
  }

  public RedisConfiguration getRedisConfig() {
    var env = getEnv();
    if (env.get("REDIS_HOST") != null) {
      return RedisConfiguration
          .builder()
          .host(env.get("REDIS_HOST"))
          .port(Integer.parseInt(env.get("REDIS_PORT")))
          .auth(env.get("REDIS_AUTH"))
          .build();
    } else {
      return RedisConfiguration
          .builder()
          .host(System.getenv("REDIS_HOST"))
          .port(Integer.parseInt(System.getenv("REDIS_PORT")))
          .auth(System.getenv("REDIS_AUTH"))
          .build();
    }
  }

  public Dotenv getEnv() {
    return Dotenv.load();
  }
}
