package ceres;

import ceres.repository.PoemRepository;
import ceres.repository.PoemRepositoryImpl;
import ceres.repository.PoetRepository;
import ceres.repository.PoetRepositoryImpl;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import java.net.UnknownHostException;

public class AppModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(PoetRepository.class).to(PoetRepositoryImpl.class);
    bind(PoemRepository.class).to(PoemRepositoryImpl.class);
  }

  @Provides
  public MongoClient mongoClient() throws UnknownHostException {
    return new MongoClient(new MongoClientURI(System.getenv("MONGODB_URL")));
  }
}
