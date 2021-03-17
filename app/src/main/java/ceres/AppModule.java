package ceres;

import ceres.configuration.ApplicationConfiguration;
import ceres.configuration.PoemConfiguration;
import ceres.crawler.PoemFetcher;
import ceres.crawler.PoemFetcherImpl;
import ceres.repository.PoemRepository;
import ceres.repository.PoemRepositoryImpl;
import ceres.repository.PoetRepository;
import ceres.repository.PoetRepositoryImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import java.io.IOException;
import java.net.UnknownHostException;

public class AppModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(PoetRepository.class).to(PoetRepositoryImpl.class);
    bind(PoemRepository.class).to(PoemRepositoryImpl.class);
    bind(PoemFetcher.class).to(PoemFetcherImpl.class);
  }

  public MongoClient mongoClient() throws UnknownHostException {
    return new MongoClient(new MongoClientURI(System.getenv("MONGO_URL")));
  }

  @Provides
  public DB mongoDB() throws UnknownHostException {
    var client = mongoClient();
    return client.getDB("ceres");
  }

  @Provides
  public PoemConfiguration poemConfiguration() throws IOException {
    var appConfig = ApplicationConfiguration.loadFrom("app.yaml");
    return appConfig.getCrawler().getPoem();
  }
}
